package controllers;

import java.util.List;
import java.util.Set;

import models.Event;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.code.morphia.Key;
import com.google.inject.Inject;

import dao.EventDAO;
import enums.Situation;


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
			System.out.println("Form has errors");
			Set<String> keys = filledForm.errors().keySet();
			for (Object key : keys) {
				System.out.println(key+" - "+filledForm.errors().get(key));
			}
			filledForm.errors();
			
			
			List<Event> eventsList = dao.listBySituation(Situation.STARTED,Situation.FINISHED,Situation.NEVER_STARTED,Situation.ANALYSED);
			return badRequest(views.html.index.render(filledForm,eventsList));
		}
		
		Event event = filledForm.get();
		Key<Event> key = dao.save(event);
		
		flash("success", "Event was successfully added: "+key.getId());
		return index();
	}
	
}
