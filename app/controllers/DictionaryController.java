package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Dictionary;
import models.Word;
import models.forms.AddWordForm;
import models.forms.DictionaryForm;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import play.cache.Cache;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.FillDictionaryService;
import system.Global;
import system.ReturnToView;
import system.ValidationError;
import utils.PLNUtils;
import dao.DictionaryDAO;
import dao.WordDAO;

public class DictionaryController extends Controller {

	final static Form<DictionaryForm> formDictionary = Form.form(DictionaryForm.class);
	final static Form<AddWordForm> formAddWord = Form.form(AddWordForm.class);
	
	public Result pageDetails(String dictionaryId) {
		try {
			ObjectId id = new ObjectId(dictionaryId);
			
			DictionaryDAO dao = new DictionaryDAO();
			Dictionary dictionary = dao.findById(id);
			
			if (dictionary != null) {
				
				return ok(views.html.dictionary.details.render(dictionary));
				
			} else {
				return badRequest();
			}
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
	}
	
	public Result getWords(String dictionaryId, int limit, int pageNum) {
		try {
			ObjectId id = new ObjectId(dictionaryId);
			
			DictionaryDAO dao = new DictionaryDAO();
			Dictionary dictionary = dao.findById(id);
			
			if (dictionary != null) {
				ReturnToView vo = new ReturnToView();
				
				WordDAO wordDAO = new WordDAO();
				List<Word> wordsList = wordDAO.listByDictionaryId(id, limit, (pageNum * limit));
				
				HashMap<String, Object> mapa = new HashMap<String, Object>();
				
				mapa.put("words", wordsList);
				vo.setMap(mapa);
				
				return ok(Json.toJson(vo)); 
				
			} else {
				return badRequest();
			}
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
	}
	
	public Result pageList() {
		
		DictionaryDAO dao = new DictionaryDAO();
		List<Dictionary> dictionaryList = dao.listAll();
		
		return ok(views.html.dictionary.list.render(dictionaryList));
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
			Dictionary dic = null;
			if (d.getConsiderAccents() == null) {
				dic = new Dictionary(d.getName(), d.getDescricao(), false);
			} else {
				dic = new Dictionary(d.getName(), d.getDescricao(), true);
			}
			
			List<ValidationError> result = dic.validate();
			
			if (result.isEmpty()) {
			
				if (d.getFillDictionary() == null) {
					
					DictionaryDAO dao = new DictionaryDAO();
					dao.save(dic);
					
				} else {
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
	
	/**
	 * Adiciona uma word ao dicionario
	 * @param word
	 * @return
	 */
	public Result addWord() {
		
		Form<AddWordForm> form = formAddWord.bindFromRequest();
		
		AddWordForm addWordForm = form.get();
		String wordString = addWordForm.getWordString();
		
		try {
			
			ObjectId id = new ObjectId(addWordForm.getDictionaryId());
			
			DictionaryDAO dao = new DictionaryDAO();
			Dictionary dictionary = dao.findById(id);
			
			if (dictionary != null) {
				ReturnToView vo = new ReturnToView();
				
				Word word = new Word(wordString);
				word.setDictionary(new Key<Dictionary>(Dictionary.class, id));
				
				if (dictionary.isConsiderAccents() == false) {
					word.setName(PLNUtils.removeAccents(word.getName()));
				}
				
				List<ValidationError> result = word.validate();
				
				if (result.isEmpty()) {
					
					WordDAO wordDAO = new WordDAO();
					wordDAO.save(word);
					
					dictionary.setTotalWords(dictionary.getTotalWords() + 1);
					dao.save(dictionary);
					Global.loadDictionariesAndAbbreviations();
					vo.setMessage("Word added successfully to the dictionary");
				} else {
					// validation failed...
					vo.setCode(400);
					vo.setMessage("Ooops... something wrong!");
					HashMap<String, Object> mapa = new HashMap<String, Object>();
					mapa.put("errors", result);
					vo.setMap(mapa);
				}
				
				
				return ok(Json.toJson(vo)); 
				
			} else {
				return badRequest();
			}
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return badRequest();
		}
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
