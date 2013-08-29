package dao;

import java.util.List;

import models.Dictionary;
import models.Word;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import dao.base.BaseDAO;

public class WordDAO extends BaseDAO<Word> {

	public WordDAO() {
		super(Word.class);
	}

	public List<Word> listByDictionaryId(ObjectId dictionaryId) {
		
		List<Word> words = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).asList();
		
		return words;
	}
	
	public List<Word> listByDictionaryId(ObjectId dictionaryId, int limit, int offset) {

		List<Word> words = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).limit(limit).offset(offset).order("name").asList();
		
		return words;
	}


	public long countWords(ObjectId dictionaryId) {
		long wordsCount = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).filter("removed", false).countAll();
		return wordsCount;
	}


	public Word findByName(String wordString, ObjectId dictionaryId) {

		
//		Word word = createQuery().field("dictionary").equal(new Key<Dictionary>(Dictionary.class, dictionaryId)).field("name").equal(wordString.toLowerCase()).get();
		
		Word word = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).filter("name", wordString.toLowerCase()).get();
		
		return word;
	}


	
}
