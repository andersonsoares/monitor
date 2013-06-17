package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.Dictionary;
import models.Event;
import models.forms.GetTweetsForm;

import org.bson.types.ObjectId;

import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.GetTweetsService;
import system.ChartParam;
import system.ReturnToView;
import system.ValidationError;
import utils.DateUtils;

import com.google.code.morphia.Key;
import com.google.inject.Inject;

import dao.DictionaryDAO;
import dao.EventDAO;
import dao.TweetDAO;
import enums.TypeEnum;

public class EventController extends Controller {

	@Inject
	private EventDAO dao;
	
	final static Form<Event> eventForm = Form.form(Event.class);
	final static Form<GetTweetsForm> getTweetsForm = Form.form(GetTweetsForm.class);
	
	public Result getTweetsInJson(String filename) {		
		File file = new File(filename);
		
		if (file.exists()) {
			response().setContentType("application/x-download"); 
			response().setHeader("Content-disposition","attachment; filename="+file.getName());
			
			Cache.remove("getTweetStatus");
			
			return ok(file);
		} else {
			return notFound();
		}
	}
	
	public Result getTweetStatus() {
		ReturnToView vo = (ReturnToView) Cache.get("getTweetStatus");
		
		if (vo == null) {
			vo = new ReturnToView();
			vo.setMessage("null");
		} 
		return ok(Json.toJson(vo));
	}
	
	public Result getTweets() {
		
		try {
			ReturnToView vo = new ReturnToView();
			
			Form<GetTweetsForm> form = getTweetsForm.bindFromRequest();
			GetTweetsForm getTweetsForm = form.get();
			boolean isRecoverAll = getTweetsForm.isRecoverAll();
			float correctRate = getTweetsForm.getCorrectRate();
			Date startDate = getTweetsForm.getStartDate();
			Date finishDate = getTweetsForm.getFinishDate();
			
			ArrayList<ValidationError> errors =  new ArrayList<ValidationError>();
			if ((isRecoverAll == false) && (correctRate < 0 || correctRate > 100)) {
				errors.add(new ValidationError("correctRate", "Invalid correctRate"));				
			}
			
			if (Cache.get("getTweetStatus") != null) {
				errors.add(new ValidationError("none", "Get tweets already running..."));
			}
			
			if (errors.size() > 0) {
				vo.setCode(400);
				HashMap<String, Object> mapa = new HashMap<String, Object>();
				
				mapa.put("errors", errors);
				vo.setMap(mapa);
				return ok(Json.toJson(vo));
			}
			
			List<String> considerWhat = getTweetsForm.getConsiderWhat();
			ObjectId eventId = new ObjectId(getTweetsForm.getEventId());
			
			Event event = new EventDAO().findById(eventId);
			
			if (event != null) {
				ObjectId dictionaryId = new ObjectId(getTweetsForm.getDictionaryId());
				Dictionary dictionary = new DictionaryDAO().findById(dictionaryId);
				
				if (dictionary != null) {
					
					Akka.system().scheduler().scheduleOnce(
							Duration.create(0, TimeUnit.SECONDS), 
							new GetTweetsService(event, startDate, finishDate, isRecoverAll, dictionary, correctRate, considerWhat),
							Akka.system().dispatcher());
					
					
					vo.setMessage("Processando");
					
					return ok(Json.toJson(vo));
					
				} else {
					return badRequest();
				}
			} else {
				return badRequest();
			}
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
	}
	
	public Result pageDetails(String eventId) {
		
		try {
			ObjectId id = new ObjectId(eventId);
			
			EventDAO eventDAO = new EventDAO();
			Event event = eventDAO.findById(id);
			
			if (event != null) {
				
				DictionaryDAO dictionaryDAO = new DictionaryDAO();
				
				List<Dictionary> dictionariesList = dictionaryDAO.listAll();
				
				return ok(views.html.events.details.render(event, dictionariesList));
				
				
			} else {
				// event doesnt exist
				return badRequest();
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
		
	}
	
	public Result pageCreate() {
		return ok(views.html.events.create.render(eventForm));
	}
	
	public Result save() {
		
		Form<Event> filledForm = eventForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.events.create.render(filledForm));
		}
		
		
		Event event = filledForm.get();
		
		// transform keywords to lowercase
		HashMap<String, TypeEnum> keywordsLowerCase = new HashMap<String, TypeEnum>();
		
		HashMap<String, TypeEnum> keywords = event.getKeywords();
		
		Set<String> keywordsKey = keywords.keySet();
		for (String k : keywordsKey) {
			keywordsLowerCase.put(k.toLowerCase(), keywords.get(k));
		}
		
		
		event.setKeywords(keywordsLowerCase);
		
		Key<Event> key = dao.save(event);
		
		
		flash("success", "Event was successfully added: "+key.getId());
		return  redirect(controllers.routes.Application.index());
	}
	
	
	public Result getTweetsCountPerDay(String eventId) {
		
		long init = System.currentTimeMillis();
		try {
			ObjectId _eventId = new ObjectId(eventId);
			EventDAO eventDAO = new EventDAO();
			Event event = eventDAO.findById(_eventId);
			
			TweetDAO tweetDAO = new TweetDAO();
			
			ReturnToView vo = new ReturnToView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<ChartParam> chartParams = new ArrayList<ChartParam>();
			
			
			Date aux = event.getStartDate();
			Date finishDate = event.getFinishDate();
			Date now = new Date(System.currentTimeMillis());
			if (now.before(finishDate)) {
				finishDate = now;
			}
			
			chartParams.add(new ChartParam(aux.getTime(), tweetDAO.countInInterval(_eventId, event.getStartDate(), DateUtils.getFinalOfTheDay(event.getStartDate()))));
			
			
			
			
			
			while(true) {
				aux = DateUtils.getInitNextDay(aux);
				Date finalDia = DateUtils.getFinalOfTheDay(aux);
				if (finalDia.after(finishDate)) {
					chartParams.add(new ChartParam(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, finishDate)));
					break;
				}
				chartParams.add(new ChartParam(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, finalDia)));
			}
			
			System.out.println("Elapsed time: "+(System.currentTimeMillis() - init));
			map.put("countPerDay", chartParams);
			vo.setMap(map);
			return ok(Json.toJson(vo));
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return notFound();
		}
	}
	
	
	
}
