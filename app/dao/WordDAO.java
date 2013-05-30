package dao;

import models.Word;
import dao.base.BaseDAO;

public class WordDAO extends BaseDAO<Word> {

	public WordDAO() {
		super(Word.class);
	}

}
