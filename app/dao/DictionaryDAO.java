package dao;

import models.Dictionary;
import dao.base.BaseDAO;

public class DictionaryDAO extends BaseDAO<Dictionary> {

	public DictionaryDAO() {
		super(Dictionary.class);
	}

}
