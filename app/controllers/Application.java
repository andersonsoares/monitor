package controllers;

import java.util.List;

import models.Event;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.inject.Inject;

import dao.EventDAO;
import enums.Situation;


public class Application extends Controller {


	@Inject
	private EventDAO dao;
	
	public Result index() {

		List<Event> eventsList = dao.listBySituation(Situation.STARTED,Situation.FINISHED,Situation.NEVER_STARTED,Situation.ANALYSED);
		
		return ok(views.html.events.list.render(eventsList));
		
	}
	
}