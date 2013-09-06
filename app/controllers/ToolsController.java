package controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import models.Abbreviation;
import models.Dictionary;
import models.forms.AnalyseCheckForm;
import play.cache.Cache;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.AnalyseCheck;
import system.ReturnToView;
import utils.PLNUtils;
import dao.DictionaryDAO;

/**
 * Class that control tools like service to normalize tweets
 * @author aers
 *
 */
public class ToolsController extends Controller {
	
	final static Form<AnalyseCheckForm> analyseCheckForm = Form.form(AnalyseCheckForm.class);
	
	public Result pageAnalyseCheck() {
		DictionaryDAO dictionaryDAO = new DictionaryDAO();
		
		List<Dictionary> dictionariesList = dictionaryDAO.listAll();
		return ok(views.html.analyseCheck.analyseForm.render(dictionariesList));
	}
	
	public Result analyseCheck() {
		
		Form<AnalyseCheckForm> formAnalyseCheck = analyseCheckForm.bindFromRequest();
		
		AnalyseCheckForm form = formAnalyseCheck.get();
		
		String tweet = form.getTweet();
		List<String> considerWhat = form.getConsiderWhat();
		boolean considerHashtag = considerWhat.contains("hashtags");
		boolean considerUser = considerWhat.contains("users");
		boolean considerUrl = considerWhat.contains("urls");
		boolean considerSigla = considerWhat.contains("siglas");
		
		String _dictionaryId = form.getDictionaryId();
		@SuppressWarnings("unchecked")
		AnalyseCheck analyseCheck = PLNUtils.analyseCheck(tweet, (HashSet<String>)Cache.get(_dictionaryId), (List<Abbreviation>)Cache.get("abbreviations"), considerHashtag, considerUser, considerUrl, considerSigla);
		
		ReturnToView vo = new ReturnToView();
		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("analyseCheck", analyseCheck);
		vo.setMap(map);
		return ok(Json.toJson(vo));
	}
	

}
