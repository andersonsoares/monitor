package dao;

import models.Abbreviation;
import dao.base.BaseDAO;

public class AbbreviationDAO extends BaseDAO<Abbreviation> {

	public AbbreviationDAO() {
		super(Abbreviation.class);
	}

}
