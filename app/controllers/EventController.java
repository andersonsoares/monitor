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
import models.EventAnalysis;
import models.GraphicPoint;
import models.Tweet;
import models.forms.AnalyseForm;
import models.forms.GetTweetsForm;

import org.bson.types.ObjectId;

import play.Logger;
import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.AnalyseEventService;
import services.GetTweetsService;
import system.ReturnToView;
import system.ValidationError;
import utils.DateUtils;

import com.google.code.morphia.Key;
import com.google.inject.Inject;
import com.mongodb.WriteResult;

import dao.DictionaryDAO;
import dao.EventAnalysisDAO;
import dao.EventDAO;
import dao.TweetDAO;
import enums.SentimentEnum;
import enums.TypeEnum;

public class EventController extends Controller {

	@Inject
	private EventDAO dao;
	
	final static Form<Event> eventForm = Form.form(Event.class);
	final static Form<GetTweetsForm> getTweetsForm = Form.form(GetTweetsForm.class);
	final static Form<AnalyseForm> analyseForm = Form.form(AnalyseForm.class);
	
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
	
	/*
	 * Method that receive the form params to analyse the event tweets
	 * And call the AnalyseEventService to do the job done! :)
	 */
	public Result analyse() {
		
		Form<AnalyseForm> form = analyseForm.bindFromRequest();
		AnalyseForm analyseForm = form.get();
		
		ReturnToView vo = new ReturnToView();
		
		
		float correctRate = analyseForm.getCorrectRate();
		if (correctRate < 0 || correctRate > 100) {
			vo.setCode(400);
			vo.setMessage("Correct Rate must be > 0 and < 100");
			return ok(Json.toJson(vo));
		}
		
		List<String> considerWhat = analyseForm.getConsiderWhat();
		String email = analyseForm.getEmail();
		ObjectId eventId = new ObjectId(analyseForm.getEventId());
		
		Event event = new EventDAO().findById(eventId);
		
		if (event != null) {
			
			Date startDate = analyseForm.getStartDate();
			Date finishDate = analyseForm.getFinishDate();
			
			if (startDate != null && finishDate != null && startDate.after(finishDate)) {
				vo.setCode(400);
				vo.setMessage("Start date cant be after Finish date");
				return ok(Json.toJson(vo));
			}
			
			if (startDate == null || startDate.before(event.getStartDate())) {
				startDate = event.getStartDate();
			}
			if (finishDate == null || finishDate.after(event.getFinishDate())) {
				finishDate = event.getFinishDate();
			}
			
			ObjectId dictionaryId = new ObjectId(analyseForm.getDictionaryId());
			Dictionary dictionary = new DictionaryDAO().findById(dictionaryId);
			
			if (dictionary != null) {
				
				Logger.info("Request analyse to '"+event.getName()+"' from: "+request().remoteAddress());
				
				Akka.system().scheduler().scheduleOnce(
						Duration.create(0, TimeUnit.SECONDS), 
						new AnalyseEventService(event, dictionary, correctRate, considerWhat, email, startDate, finishDate),
						Akka.system().dispatcher());
				
				if (email.isEmpty()) {
					vo.setMessage("Analysis is running. This might take a while. Come back in a few minutes.");
				} else {
					vo.setMessage("Analysis is running. You will be notified when it finish at '"+email.toLowerCase());
				}
				return ok(Json.toJson(vo));
				
			}
		} 
		return badRequest();
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
				
				EventAnalysisDAO analysisDAO = new EventAnalysisDAO();
				
				int count = (int) analysisDAO.getCountByEvent(id);
				
				return ok(views.html.events.details.render(event, count));
				
				
			} else {
				// event doesnt exist
				return notFound();
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
		
	}
	
	public Result pageAnalysis(String eventId, String orderBy, String order) {
		
		ObjectId id = new ObjectId(eventId);
		EventDAO eventDAO = new EventDAO();
		Event event = eventDAO.findById(id);
		
		if (event != null) {
			DictionaryDAO dictionaryDAO = new DictionaryDAO();
			List<Dictionary> dictionaryList = dictionaryDAO.listAll();
			
			EventAnalysisDAO analysisDAO = new EventAnalysisDAO();
			List<EventAnalysis> analysisList;
			if (!orderBy.equals("createdAt") && !orderBy.equals("totalTweetsAnalysed") && !orderBy.equals("correctRate")) {
				analysisList = analysisDAO.listAllFromEvent(id);
			} else {
				if (!order.equals("asc") && !order.equals("desc"))
					analysisList = analysisDAO.listAllFromEvent(id,orderBy);
				else {
					if (order.equals("asc")) { 
						analysisList = analysisDAO.listAllFromEvent(id,orderBy);						
					} else {
						analysisList = analysisDAO.listAllFromEvent(id,"-"+orderBy);
					}
				}
			}
			int average = 0;
			for (EventAnalysis eventAnalysis : analysisList) {
				average+= eventAnalysis.getEllapsedTime();
			}
			if (analysisList.size() != 0) {
				average = average/analysisList.size();
			}
			
			return ok(views.html.events.analysis.list.render(event, analysisList, average, dictionaryList));
		}
		
		return notFound();
	}

	
	public Result pageAnalysisDetails(String eventId, String eventAnalysisId, String kind, int page, int pageLength) {
		long i = System.currentTimeMillis();
		
		ObjectId id = new ObjectId(eventId);
		EventDAO eventDAO = new EventDAO();
		Event event = eventDAO.findById(id);
		
		if (event != null) {
			EventAnalysisDAO analysisDAO = new EventAnalysisDAO();
			
			ObjectId _eventAnalysisId = new ObjectId(eventAnalysisId);
			EventAnalysis eventAnalysis = analysisDAO.findById(_eventAnalysisId);
			
			if (eventAnalysis != null) {
				
				TweetDAO tweetDAO = new TweetDAO();
				
				int total = 0;
				List<Tweet> tweetsList;
				
				long init = System.currentTimeMillis();
				Logger.info(init-i+" ms"); 
				
				if (kind.equals("all") || (!kind.equals("positives") && !kind.equals("negatives") && !kind.equals("neutral") && !kind.equals("incorrect") && !kind.equals("all"))) {
					total = eventAnalysis.getTotalTweetsAnalysed();
					tweetsList = tweetDAO.getTweetsAfterAnalysedBy(event.getId(), eventAnalysis.getId(), page-1, pageLength);
					return ok(views.html.events.analysis.details.render(event, eventAnalysis, tweetsList, "all",page,pageLength,total));
					
				} else {
					SentimentEnum sentiment = null;
					if (kind.equals("positives")) {
						sentiment = SentimentEnum.POSITIVE;
						total = eventAnalysis.getTotalPositives();
					} else if(kind.equals("negatives")) {
						sentiment = SentimentEnum.NEGATIVE;
						total = eventAnalysis.getTotalNegatives();
					} else if(kind.equals("neutral")) {
						sentiment = SentimentEnum.NEUTRAL;
						total = eventAnalysis.getTotalNeutral();
					} else if(kind.equals("incorrect")) {
						sentiment = SentimentEnum.INCORRECT;
						total = eventAnalysis.getTotalIncorrect();
					}
					
					tweetsList = tweetDAO.getTweetsAfterAnalysedByWithSentiment(event.getId(), eventAnalysis.getId(), sentiment, page-1, pageLength);
					
					long end = System.currentTimeMillis();
					Logger.info(end-init+" ms to load tweets");
					return ok(views.html.events.analysis.details.render(event, eventAnalysis, tweetsList, kind,page,pageLength,total));
				}
				
			}
			
		}
		
		return notFound();
	}
	
public Result pageTeste(String eventId) {
		
		try {
			ObjectId id = new ObjectId(eventId);
			
			EventDAO eventDAO = new EventDAO();
			Event event = eventDAO.findById(id);
			
			if (event != null) {
				
				DictionaryDAO dictionaryDAO = new DictionaryDAO();
				
				List<Dictionary> dictionariesList = dictionaryDAO.listAll();
				
				return ok(views.html.events.teste.render(event, dictionariesList));
				
				
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
	
	public Result getTotalDiffUsers(String eventId) {
		ObjectId _eventId = new ObjectId(eventId);
		EventDAO eventDAO = new EventDAO();
		Event event = eventDAO.findById(_eventId);
		
		if (event != null) {
			
			long total = 0;
			if (event.getNrTweets() > event.getTotalTweetsLastDiffUsersCreation()) {
				Logger.info(event.getName()+" - Atualizando total de usuarios diferentes");
				TweetDAO tweetDAO = new TweetDAO();
				total = tweetDAO.countTotalDiffUsers(_eventId);
				event.setTotalDiffUsers((int)total);
				event.setTotalTweetsLastDiffUsersCreation(event.getNrTweets());
				eventDAO.save(event);
			} else {				
				total = event.getTotalDiffUsers();
			}
			
			return ok(Json.toJson(total));
		}
		return notFound();
	}
	
	public Result getTweetsCountPerDay(String eventId) {
		
		ReturnToView vo = new ReturnToView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			ObjectId _eventId = new ObjectId(eventId);
			EventDAO eventDAO = new EventDAO();
			Event event = eventDAO.findById(_eventId);
			
			if (event.getNrTweets() > event.getTotalTweetsLastGraphicCreation()) {
				Logger.info(event.getName()+": Novos tweets! Gerando grafico novamente...");

				TweetDAO tweetDAO = new TweetDAO();
				
				List<GraphicPoint> graphicPoints = new ArrayList<GraphicPoint>();
				
				Date aux = event.getStartDate();
				Date finishDate = event.getFinishDate();
				Date now = new Date(System.currentTimeMillis());
				if (now.before(finishDate)) {
					finishDate = now;
				}
				
				graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, event.getStartDate(), DateUtils.getFinalOfTheDay(event.getStartDate()))));
				
				while(true) {
					aux = DateUtils.getInitNextDay(aux);
					Date finalDia = DateUtils.getFinalOfTheDay(aux);
					if (finalDia.after(finishDate)) {
						graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, finishDate)));
						break;
					}
					graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, finalDia)));
				}
				
				event.setGraphicPoints(graphicPoints);
				event.setTotalTweetsLastGraphicCreation(event.getNrTweets());
				eventDAO.save(event);
				
				map.put("countPerDay", graphicPoints);
			} else {
				map.put("countPerDay", event.getGraphicPoints());
			}
			
			vo.setMap(map);
			return ok(Json.toJson(vo));
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return notFound();
		}
	}
	
	public Result getTweetsByDay(String eventId) {
		
		try {
			
			ObjectId _eventId = new ObjectId(eventId);
			EventDAO eventDAO = new EventDAO();
			Event event = eventDAO.findById(_eventId);
			
			if (event != null) {
				
				ReturnToView vo = new ReturnToView();
				HashMap<String, Object> map = new HashMap<String, Object>();
		
				Long dateInMillis = Long.parseLong(request().getQueryString("date"));
				
				Date startDate = new Date(dateInMillis);
				Date aux = startDate;
				Date finishDate = DateUtils.getFinalOfTheDay(startDate);
				Date now = new Date(System.currentTimeMillis());
				
				if (now.after(finishDate)) {			
					TweetDAO tweetDAO = new TweetDAO();
					List<GraphicPoint> graphicPoints = new ArrayList<GraphicPoint>();
					
					graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, startDate, DateUtils.getNextHour(startDate))));
					
					while (aux.before(finishDate)) {
						aux = DateUtils.getNextHour(aux);
						Date nextHour = DateUtils.getNextHour(aux);
						if (nextHour.after(finishDate)) {
							graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, finishDate)));
							break;
						}
						graphicPoints.add(new GraphicPoint(aux.getTime(), tweetDAO.countInInterval(_eventId, aux, nextHour)));
					}
					
					map.put("countPerHour", graphicPoints);
					vo.setMap(map);
					
					return ok(Json.toJson(vo));
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return notFound();
	}
	
	
	public Result removeAnalysis(String _eventId, String _eventAnalysisId) {
		
		EventDAO eventDAO = new EventDAO();
		EventAnalysisDAO analysisDAO = new EventAnalysisDAO();
		ObjectId eventId = new ObjectId(_eventId);
		Event event = eventDAO.findById(eventId);
		ObjectId eventAnalysisId = new ObjectId(_eventAnalysisId);
		if (event != null) {
			
			EventAnalysis eventAnalysis = analysisDAO.findById(eventAnalysisId);
			
			if (eventAnalysis != null) {
				
				// atualizar todos os tweets que foram analisados por esta analise
				// e remover a analise :P
				TweetDAO tweetDAO = new TweetDAO();
				tweetDAO.removeEventAnalysis(eventId, eventAnalysisId);
				WriteResult res = analysisDAO.delete(eventAnalysis);
				Logger.info("Event analysis removed: "+res);
			}
			
		}
		
		return badRequest();
	}
	
	
	
}
