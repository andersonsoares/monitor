package controllers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Event;
import models.Glossary;
import models.Word;

import org.bson.types.ObjectId;

import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.WordService;
import system.ReturnToView;
import dao.EventDAO;
import dao.WordDAO;
import enums.Situation;

public class GlossaryController extends Controller {
	
	static final Form<Glossary> glossaryForm = Form.form(Glossary.class);

	public Result pageListWords() {
	
		WordDAO dao = new WordDAO();
		
		List<Word> wordsList = dao.createQuery().order("-count").limit(100).offset(0).asList();
		long wordsCount = dao.createQuery().countAll();
		
		return ok(views.html.glossary.pageListWords.render(wordsList, wordsCount,1,100,"count"));
		
	}
	
	public Result pagePaginateWords(int pageNum, int pageSize, String orderType) {
		
		if (orderType == null || orderType.isEmpty() || pageNum < 1 || pageSize < 1) {
			return pageListWords();
		} 
		
		if (!orderType.equals("count") && !orderType.equals("name")) {
			return pageListWords();
		}
		
		WordDAO dao = new WordDAO();
		
		String order;
		if (orderType.equals("count")) {
			order = "-count";
		} else {
			order = "name";
		}
		
		int offset = pageNum - 1;
		for( int i = 0; i < pageNum - 1; i++) {
			offset += pageSize;
		}
		
		List<Word> wordsList = dao.createQuery().order(order).limit(pageSize).offset(offset).asList();
		long wordsCount = dao.createQuery().countAll();
		
		return ok(views.html.glossary.pageListWords.render(wordsList, wordsCount, pageNum, pageSize, orderType));
		
	}
	
	public Result pageGenerateWords() {
		
		EventDAO eventDAO = new EventDAO();
		
		List<Event> eventsList = eventDAO.listBySituation(Situation.STARTED, Situation.FINISHED);
		
		return ok(views.html.glossary.pageGenerateWords.render(eventsList));
		
	}
	
	public Result generateWords() {
		
		ReturnToView vo = new ReturnToView();
	
		Form<Glossary> filledForm = glossaryForm.bindFromRequest();
		
		Glossary glossary = filledForm.get();
		
		for (ObjectId str : glossary.getEventsList()) {
			System.out.println(str.toString());
		}
		
		if (glossary.getEventsList() != null && !glossary.getEventsList().isEmpty()) {
				
			Akka.system().scheduler().scheduleOnce(
					Duration.create(0, TimeUnit.SECONDS), 
					new WordService(glossary),
					Akka.system().dispatcher());
			
			vo.setMessage("Starting");
			vo.setRedirectUrl(routes.GlossaryController.pageListWords().toString());
			
		} else {
			vo.setCode(400);
			vo.setMessage("You must select at least ONE event!");
		}
		
		return ok(Json.toJson(vo));
	
	}
	
	public Result status() {
		ReturnToView vo = new ReturnToView();
		
		vo.setMessage(Cache.get("generateWordsProgress").toString());
		
		return ok(Json.toJson(vo));
	}
	
}
