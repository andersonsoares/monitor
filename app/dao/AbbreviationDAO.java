package dao;

import java.util.List;

import models.Abbreviation;
import dao.base.BaseDAO;

public class AbbreviationDAO extends BaseDAO<Abbreviation> {

	public AbbreviationDAO() {
		super(Abbreviation.class);
	}

	public List<Abbreviation> paginate(int page, int limit) {
		return createQuery().filter("removed", false).limit(limit).offset(page*limit).order("name").asList();
	}

}
