package controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.Dictionary;
import models.Event;
import models.forms.GetTweetsForm;

import org.bson.types.ObjectId;

import play.data.Form;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.GetTweetsService;
import services.TweetService;

import com.google.code.morphia.Key;
import com.google.inject.Inject;

import dao.DictionaryDAO;
import dao.EventDAO;
import enums.TypeEnum;

public class EventController extends Controller {

	@Inject
	private EventDAO dao;
	
	final static Form<Event> eventForm = Form.form(Event.class);
	final static Form<GetTweetsForm> getTweetsForm = Form.form(GetTweetsForm.class);
	
	public Result getTweets() {
		
		try {
			Form<GetTweetsForm> form = getTweetsForm.bindFromRequest();
			GetTweetsForm getTweetsForm = form.get();
			
			boolean isRecoverAll = getTweetsForm.isRecoverAll();
			List<String> considerWhat = getTweetsForm.getConsiderWhat();
			float correctRate = getTweetsForm.getCorrectRate();
			ObjectId eventId = new ObjectId(getTweetsForm.getEventId());
			
			Event event = new EventDAO().findById(eventId);
			
			if (event != null) {
				ObjectId dictionaryId = new ObjectId(getTweetsForm.getDictionaryId());
				Dictionary dictionary = new DictionaryDAO().findById(dictionaryId);
				
				if (dictionary != null) {
					
					Akka.system().scheduler().scheduleOnce(
							Duration.create(0, TimeUnit.SECONDS), 
							new GetTweetsService(event, isRecoverAll, dictionary, correctRate, considerWhat),
							Akka.system().dispatcher());
					
					return ok("Analisando");
					
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
	
	public Result downloadTweets(String eventId) {
		
		TweetService service = new TweetService();
		
		File file = service.createFileWithTweetsFrom(new ObjectId(eventId));
		
		response().setContentType("application/x-download"); 
		response().setHeader("Content-disposition","attachment; filename="+file.getName());
		
		return ok(file);
	}
	
}
