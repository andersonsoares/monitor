package controllers;

import java.io.File;
import java.util.HashMap;
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
import enums.TypeEnum;

public class EventController extends Controller {

	@Inject
	private EventDAO dao;
	
	final static Form<Event> eventForm = Form.form(Event.class);
	
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
