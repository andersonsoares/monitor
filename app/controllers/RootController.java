package controllers;

import java.util.List;

import models.Event;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.RootService;
import views.roots.forms.GenerateRootForm;
import dao.EventDAO;
import enums.Situation;

public class RootController extends Controller {

	final static Form<GenerateRootForm> generateRootForm = Form.form(GenerateRootForm.class);
	
	public Result pageGenerate() {
		
		EventDAO eventDAO = new EventDAO();
		
		List<Event> eventsList = eventDAO.listBySituation(Situation.STARTED, Situation.FINISHED);
		
		return ok(views.html.roots.index.render(eventsList));
	}
 	
	
	public Result generate() {
		
		Form<GenerateRootForm> filledForm = generateRootForm.bindFromRequest();
		
		try {
			
			GenerateRootForm generateRootFormFilled = filledForm.get();
			
			if (generateRootFormFilled != null) {
				List<ObjectId> eventsList = generateRootFormFilled.getSelectedEvents();
				int cutValue = generateRootFormFilled.getCutValue();
				if (eventsList != null) {
				
					RootService rootService = new RootService();
					rootService.generate(eventsList, cutValue);
					
				}
			}
			
		} catch(Exception e) {
			flash().put("error", "You must select at least one event");
			Logger.error(e.getMessage());
			e.printStackTrace();
			return pageGenerate();
		}
		return ok("This might take a while...");
	}
}
