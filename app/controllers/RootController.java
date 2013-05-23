package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Event;
import models.Root;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import ptstemmer.Stemmer.StemmerType;
import services.RootService;
import views.roots.forms.GenerateRootForm;
import dao.EventDAO;
import dao.RootDAO;
import enums.Situation;

public class RootController extends Controller {

	final static Form<GenerateRootForm> generateRootForm = Form.form(GenerateRootForm.class);
	
	
	public Result acceptRoot(String rootId) {
		
		RootDAO dao = new RootDAO();
		
		
		boolean removed = dao.setRemoved(new ObjectId(rootId));
		
		Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put("status", "ok");
		retorno.put("accepted", removed);
		
		return ok(Json.toJson(retorno));
	}
	
	
	public Result pageGenerate() {
		
		EventDAO eventDAO = new EventDAO();
		
		List<Event> eventsList = eventDAO.listBySituation(Situation.STARTED, Situation.FINISHED);
		
		return ok(views.html.roots.index.render(eventsList, Arrays.asList(StemmerType.values())));
	}
 	
	
	public Result list() {
		
		RootDAO dao = new RootDAO();
		
		List<Root> rootsList = dao.createQuery().order("-count").limit(100).offset(0).asList();
		long rootsCount = dao.createQuery().countAll();
		
		long rootsCountRejected = dao.createQuery().filter("removed", true).countAll();
		
		return ok(views.html.roots.list.render(rootsList, rootsCountRejected, rootsCount,1,100,"count"));
	}
	
	
	public Result paginate(int pageNum, int pageSize, String orderType) {
		
		if (orderType == null || orderType.isEmpty() || pageNum < 1 || pageSize < 1) {
			return list();
		} 
		
		if (!orderType.equals("count") && !orderType.equals("rootWord")) {
			return list();
		}
		
		RootDAO dao = new RootDAO();
		
		String order;
		if (orderType.equals("count")) {
			order = "-count";
		} else {
			order = "rootWord";
		}
		
		int offset = pageNum - 1;
		for( int i = 0; i < pageNum - 1; i++) {
			offset += pageSize;
		}
		
		List<Root> rootsList = dao.createQuery().order(order).limit(pageSize).offset(offset).asList();
		long rootsCount = dao.createQuery().countAll();
		long rootsCountRejected = dao.createQuery().filter("removed", true).countAll();
		return ok(views.html.roots.list.render(rootsList, rootsCountRejected, rootsCount, pageNum, pageSize, orderType));
	}
	
	
	public Result generate() {
		
		Form<GenerateRootForm> filledForm = generateRootForm.bindFromRequest();
		
		try {
			
			GenerateRootForm generateRootFormFilled = filledForm.get();
			
			if (generateRootFormFilled != null) {
				List<ObjectId> eventsList = generateRootFormFilled.getSelectedEvents();
				int cutValue = generateRootFormFilled.getCutValue();
				boolean removeAcentuation = generateRootFormFilled.isRemoveAcentuation();
				StemmerType algoritmo = generateRootFormFilled.getAlgoritm();
				System.out.println(algoritmo.name());
				System.out.println(removeAcentuation);
				if (eventsList != null) {
				
					RootService rootService = new RootService();
					rootService.generate(eventsList, cutValue, algoritmo);
					
				}
			}
			
		} catch(Exception e) {
			flash().put("error", "You must select at least one event");
			Logger.error(e.getMessage());
			e.printStackTrace();
			return pageGenerate();
		}
		return list();
	}
}
