package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Dictionary;
import models.forms.DictionaryForm;

import org.bson.types.ObjectId;

import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.FillDictionaryService;
import system.ReturnToView;
import system.ValidationError;
import dao.DictionaryDAO;

public class DictionaryController extends Controller {

	final static Form<DictionaryForm> formDictionary = Form.form(DictionaryForm.class);
	
	public Result pageDetails(String dictionaryId) {
		try {
			ObjectId id = new ObjectId(dictionaryId);
			
			DictionaryDAO dao = new DictionaryDAO();
			Dictionary dictionary = dao.findById(id);
			
			if (dictionary != null) {
				System.out.println(dictionary.getName());
				return ok(views.html.dictionary.details.render(dictionary));
			} else {
				return badRequest();
			}
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
	}
	
	public Result pageCreate() {
		return ok(views.html.dictionary.create.render());
	}
	
	/**
	 * Cria um dicionario
	 * @return
	 */
	public Result create() {
		
		ReturnToView vo = new ReturnToView();
		
		Object progress = Cache.get("createDictionaryProgress");
		
		if (progress == null) {
		
			Form<DictionaryForm> form = formDictionary.bindFromRequest();
			DictionaryForm d = form.get();
			
			Dictionary dic = new Dictionary(d.getName(), d.getDescricao());
			
			List<ValidationError> result = dic.validate();
			
			if (result.isEmpty()) {
			
				if (d.getFillDictionary() == null) {
					
					DictionaryDAO dao = new DictionaryDAO();
					dao.save(dic);
					
				} else {
					System.out.println();
					// start scheduler to fill dictionary with 'dictionary.txt'
					
					Akka.system().scheduler().scheduleOnce(
							Duration.create(0, TimeUnit.SECONDS), 
							new FillDictionaryService(dic),
							Akka.system().dispatcher());
					
					
				}
				
				vo.setMessage("Dictionary saved successfully");
				
			} else {
				// validation failed...
				vo.setCode(400);
				vo.setMessage("Ooops... something wrong!");
				HashMap<String, Object> mapa = new HashMap<String, Object>();
				mapa.put("errors", result);
				vo.setMap(mapa);
				
			}
			
		} else {
			// there is a creation running :/
			vo.setCode(400);
			vo.setMessage("There is a dictionary creation running... wait a few seconds and try again");
		}
		
		return ok(Json.toJson(vo));
	}
	
	
	/**
	 * Remove o dicionario
	 * @return
	 */
	public Result remove() {
		return TODO;
	}
	
	
	public Result pageAddWord() {
		return TODO;
	}
	
	/**
	 * Adiciona uma word ao dicionario
	 * @param word
	 * @return
	 */
	public Result addWord(String word) {
		return TODO;
	}
	
	/**
	 * Remove uma palavra do dicionario
	 * @param id String objectId
	 */
	public Result removeWord(String id) {
		return TODO;
	}
	
	public Result statusCreate() {
		
		ReturnToView vo = new ReturnToView();
		
		Object progress = Cache.get("createDictionaryProgress");
		
		if (progress != null) {
			vo.setMessage("Loading default dictionary and saving");
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			mapa.put("progress", progress);
			vo.setMap(mapa);
		} else {
			vo.setMessage("null");
		}
		
		return ok(Json.toJson(vo));
	}
	
}
