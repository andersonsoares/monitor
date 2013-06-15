package controllers;

import java.util.List;

import models.Abbreviation;

import org.bson.types.ObjectId;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.ReturnToView;
import dao.AbbreviationDAO;

public class AbbreviationController extends Controller {

	public Result pageList(int page, int pageLength) {
		AbbreviationDAO dao = new AbbreviationDAO();
		System.out.println(page);
		System.out.println(pageLength);
		
		List<Abbreviation> list = dao.paginate(page-1, pageLength);
		long count = dao.count();
		return ok(views.html.abbreviation.list.render(list, page, pageLength, count));
	}
	
	public Result remove(String abbreviationId) {
		System.out.println(abbreviationId);
		try {
			
			ReturnToView vo = new ReturnToView();
			
			ObjectId _abbreviationId = new ObjectId(abbreviationId);
			
			AbbreviationDAO dao = new AbbreviationDAO();
			dao.deleteById(_abbreviationId);
			
			vo.setMessage("Removed");
			
			return ok(Json.toJson(vo));
			
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return notFound();
		}
	}
	
	
}
