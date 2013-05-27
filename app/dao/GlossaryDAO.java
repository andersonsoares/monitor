package dao;

import models.Glossary;
import dao.base.BaseDAO;

public class GlossaryDAO extends BaseDAO<Glossary> {

	public GlossaryDAO() {
		super(Glossary.class);
	}

}
