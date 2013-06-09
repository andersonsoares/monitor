package services;

import java.util.ArrayList;
import java.util.HashSet;

import play.Logger;
import play.cache.Cache;

import models.Dictionary;
import models.Word;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.DictionaryDAO;
import dao.WordDAO;

public class FillDictionaryService implements Runnable {

	private Dictionary dicionario;
	
	public FillDictionaryService(Dictionary dicionario) {
		this.dicionario = dicionario;
	}
	
	@Override
	public void run() {
		
		Cache.set("createDictionaryProgress", 0.0);
		
		long initialTime = System.currentTimeMillis();
		
		DictionaryDAO dao = new DictionaryDAO();
		WordDAO wordDAO = new WordDAO();
		
		Key<Dictionary> dictionaryKey = dao.save(dicionario);
		
		HashSet<String> defaultDictionary = PLNUtils.readDictionary(true);
		
		ArrayList<Word> wordsToSave = new ArrayList<Word>();
		int i = 0;
		int totalCount = 0;
		int dicionarioSize = defaultDictionary.size();
		for (String word : defaultDictionary) {
			Word w = new Word(word);
			w.setDictionary(dictionaryKey);
			wordsToSave.add(w);

			if (i >= 10000 || totalCount == (dicionarioSize - 1)) {
				i = 0;
				wordDAO.saveCollection(wordsToSave);
				wordsToSave.clear();
			} else {
				i++;
			}
			Cache.set("createDictionaryProgress", (float) (totalCount * 100 / dicionarioSize));
			totalCount++;
		}
		
		
		Logger.info("Created array with words... now saving");
		Logger.info("Time elapsed: "+(System.currentTimeMillis() - initialTime)+" ms");
		
		defaultDictionary = null;
		wordsToSave = null;
		Logger.info("Dictionary saved");
		Logger.info("Time elapsed: "+(System.currentTimeMillis() - initialTime)+" ms");
		
		Cache.remove("createDictionaryProgress");
		
	}

}
