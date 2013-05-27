package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import models.Acronym;
import models.Event;
import models.Glossary;
import models.Root;

import org.bson.types.ObjectId;

import play.Logger;
import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import ptstemmer.Stemmer.StemmerType;
import scala.concurrent.duration.Duration;
import services.RootService;
import system.ReturnToView;
import system.StatusProgress;
import views.roots.forms.GenerateRootForm;
import dao.AcronymDAO;
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
	
	public Result listAcronym() {
		
		AcronymDAO dao = new AcronymDAO();
		
		List<Acronym> acronymsList = dao.createQuery().order("-count").limit(100).offset(0).asList();
		long acronymsCount = dao.createQuery().countAll();
		
		
		
		return ok(views.html.roots.listAcronym.render(acronymsList, acronymsCount,1,100,"count"));
	}
	

	public Result paginateAcronym(int pageNum, int pageSize, String orderType) {
		
		if (orderType == null || orderType.isEmpty() || pageNum < 1 || pageSize < 1) {
			return list();
		} 
		
		if (!orderType.equals("count") && !orderType.equals("name")) {
			return list();
		}
		
		AcronymDAO dao = new AcronymDAO();
		
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
		
		List<Acronym> acronymsList = dao.createQuery().order(order).limit(pageSize).offset(offset).asList();
		long acronymsCount = dao.createQuery().countAll();
		return ok(views.html.roots.listAcronym.render(acronymsList, acronymsCount, pageNum, pageSize, orderType));
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
		
		ReturnToView vo = new ReturnToView();
		
		if (Cache.get("generateRootProgress") == null) {
			// There is no active generation
		
			try {
				
				Form<GenerateRootForm> filledForm = generateRootForm.bindFromRequest();
				
				GenerateRootForm generateRootFormFilled = filledForm.get();
				
				if (generateRootFormFilled != null) {
					List<ObjectId> eventsList = generateRootFormFilled.getSelectedEvents();
					int cutValue = generateRootFormFilled.getCutValue();
					StemmerType algoritmo = generateRootFormFilled.getAlgoritm();
					String name = generateRootFormFilled.getName();
					
					Glossary glossary = new Glossary(name, cutValue, algoritmo, eventsList);
					
					if (eventsList != null && !eventsList.isEmpty()) {
						
						Akka.system().scheduler().scheduleOnce(
								Duration.create(0, TimeUnit.SECONDS), 
								new RootService(glossary),
								Akka.system().dispatcher());
						
						vo.setMessage("Starting");
						vo.setRedirectUrl(routes.RootController.list().toString());
						
					} else {
						vo.setCode(400);
						vo.setMessage("You must select at least ONE event!");
					}
					
					return ok(Json.toJson(vo));
					
				} else {
					return badRequest();
				}
				
			} catch(Exception e) {
				Logger.error(e.getMessage());
				e.printStackTrace();
				return status(400);
			} 
		} else {
			vo.setCode(400);
			vo.setMessage("One generation is already running. You must wait it finish to run another");
			return ok(Json.toJson(vo));
		}
		
	}
	
	/**
	 * action that verifies if there is a generation happening
	 * and returns the progress (complete %)
	 * @return
	 */
	public Result status() {
		
		ReturnToView vo = new ReturnToView();
	
		StatusProgress sp =  (StatusProgress) Cache.get("generateRootProgress");
		vo.getMap().put("statusProgress", sp);
		
		return ok(Json.toJson(vo));
	}
	
	
	
	
	
}
