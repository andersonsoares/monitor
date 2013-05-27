package dao;

import models.Acronym;
import dao.base.BaseDAO;

public class AcronymDAO extends BaseDAO<Acronym> {

	public AcronymDAO() {
		super(Acronym.class);
	}

}
