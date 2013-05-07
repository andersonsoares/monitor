package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Event;

import org.bson.types.ObjectId;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.TweetService;

import com.google.code.morphia.Key;
import com.google.inject.Inject;

import dao.EventDAO;
import enums.Situation;
import enums.TypeEnum;


public class Application extends Controller {


	final static Form<Event> eventForm = Form.form(Event.class);
	
	@Inject
	private EventDAO dao;
	
	public Result index() {
		
		
		List<Event> eventsList = dao.listBySituation(Situation.STARTED,Situation.FINISHED,Situation.NEVER_STARTED,Situation.ANALYSED);
		
		return ok(views.html.index.render(eventForm,eventsList));
		
	}
	
	public Result addEvent() {
		
		Form<Event> filledForm = eventForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			List<Event> eventsList = dao.listBySituation(Situation.STARTED,Situation.FINISHED,Situation.NEVER_STARTED,Situation.ANALYSED);
			return badRequest(views.html.index.render(filledForm,eventsList));
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
		return index();
	}
	
	public Result downloadTweets(String eventId) {
		
		TweetService service = new TweetService();
		
		service.createFileWithTweetsFrom(new ObjectId(eventId));
		
		return ok();
	}
	
}