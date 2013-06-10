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
		
		long inicio = System.currentTimeMillis();
		
		List<Word> words = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).asList();
		
		System.out.println(System.currentTimeMillis() - inicio);
		return words;
	}
	
	public List<Word> listByDictionaryId(ObjectId dictionaryId, int limit, int offset) {
		
		long inicio = System.currentTimeMillis();
		
		List<Word> words = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).limit(limit).offset(offset).order("name").asList();
		
		System.out.println(System.currentTimeMillis() - inicio);
		return words;
	}


	public long countWords(ObjectId dictionaryId) {
		long wordsCount = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).filter("removed", false).countAll();
		return wordsCount;
	}


	public Word findByName(String wordString, ObjectId dictionaryId) {
		
		System.out.println(dictionaryId.toString());
		
//		Word word = createQuery().field("dictionary").equal(new Key<Dictionary>(Dictionary.class, dictionaryId)).field("name").equal(wordString.toLowerCase()).get();
		
		Word word = createQuery().filter("dictionary",new Key<Dictionary>(Dictionary.class, dictionaryId)).filter("name", wordString.toLowerCase()).get();
		
		return word;
	}


	
}
