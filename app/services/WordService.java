package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Glossary;
import models.Tweet;
import models.Word;

import org.xeustechnologies.googleapi.spelling.Language;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellResponse;

import play.Logger;
import play.cache.Cache;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.GlossaryDAO;
import dao.TweetDAO;
import dao.WordDAO;

public class WordService implements Runnable {
	
	

	@Override
	public void run() {
		
		Cache.set("generateWordsProgress", "STARTED");
		
		try {
			
			int LIMIT = 1500;
			int MIN_TOKEN_SIZE = 3;
			
			HashMap<String, Integer> mapWords = new HashMap<String, Integer>();
			ArrayList<String> ignoredWords = new ArrayList<String>();
			
			TweetDAO tweetDAO = new TweetDAO();
			GlossaryDAO glossaryDAO = new GlossaryDAO();
			WordDAO wordDAO = new WordDAO();
			
			Key<Glossary> glossaryKey = glossaryDAO.save(glossary);
			
			long totalTweets = tweetDAO.countTweetsFromEvents(glossary.getEventsList());
			long analysedTweetCount = 0;
			
			System.out.println("Total Tweets from Selected Events: "+totalTweets);
			
				
			for (int i=0; i < totalTweets; i+=LIMIT) {
				
				List<Tweet> list = tweetDAO.listAllFrom(glossary.getEventsList(), LIMIT, i);
				
				
				for (Tweet tweet : list) {
					
					// TODO: save tweet in the normalizedForm
					String normalizedText = PLNUtils.normalizedTweet(tweet.getText());
					String[] tokens = normalizedText.split(" ");
					/*
					 * Quebrar em tokens
					 * Para cada token lowerCase < MIN_TOKEN_SIZE(?):
					 * 		verificar se já esta no 'mapWords'. Se nao estiver
					 * 			rodar o spellChecker. se o resultado for nullo > adiciona no 'mapWords'
					 * 				senao > verifica se o primeiro(?) resultado == a palavra.tolowercase(), pois existe o caso dele retornar a palavra com leta maiuscula, brasil -> Brasil
					 */
					
					for (String token : tokens) {
						
						if (token.length() >= MIN_TOKEN_SIZE) {
							token = token.toLowerCase();
							
							if (!ignoredWords.contains(token)) {
								
								if (mapWords.containsKey(token)) {
									mapWords.put(token, ( mapWords.get(token) + 1 ) );
								} else {
									
									SpellResponse result = spellChecker.check(token);
									if (result.getCorrections() == null) {
										mapWords.put(token, 1);
									} else {
										for (SpellCorrection correction : result.getCorrections()) {
											String[] values = correction.getWords();
											if (values != null && values.length != 0) {
												String firstCorrection = values[0].toLowerCase();
												firstCorrection = PLNUtils.removeAccents(firstCorrection);
												if (firstCorrection.equals(token)) {
													mapWords.put(token, 1);
												} else {
													ignoredWords.add(token);
												}
											}
										}
									} // endresults == null
								} // end mapWords contains	
							} // end if ignoredWords contains
						} // end token < min_token_size
					} // ends foreach token
					analysedTweetCount++;
					
					float progress = (analysedTweetCount * 100) / totalTweets;
					
					Cache.set("generateWordsProgress", progress);
				} // end forech tweet from limit
			}
			
			
			Logger.info("Finished analysis on each word");
			ArrayList<Word> wordsToSave = new ArrayList<Word>();
			int i = 0;
			for (String token : mapWords.keySet()) {
				Word word = new Word(token, mapWords.get(token));
				
				word.setGlossary(glossaryKey);
				wordsToSave.add(word);
				
				if (i >= LIMIT || (i == (mapWords.size() - 1))) {
					wordDAO.saveCollection(wordsToSave);
					wordsToSave.clear();
					i = 0;
				} else {
					i++;
				}
			}
			Logger.info("Accepted Words saved");
			
			wordsToSave = new ArrayList<Word>();
			i = 0;
			for (String token : ignoredWords) {
				Word word = new Word(token, mapWords.get(token));
				
				word.setRemoved(true);
				word.setGlossary(glossaryKey);
				wordsToSave.add(word);
				
				if (i >= LIMIT || (i == (mapWords.size() - 1))) {
					wordDAO.saveCollection(wordsToSave);
					wordsToSave.clear();
					i = 0;
				} else {
					i++;
				}
			}
			Logger.info("Ignored Words saved");
			
		} catch (Exception e) {
			Cache.remove("generateWordsProgress");
			Logger.error("Erro na geracao do glossario: "+e.getMessage());
			e.printStackTrace();
		}
		
		Cache.remove("generateWordsProgress");
		
	}
	
	private final SpellChecker spellChecker;
	
	private final Glossary glossary;
	
	public WordService(Glossary glossary) {
		this.glossary = glossary;
		this.spellChecker = new SpellChecker(Language.PORTUGUESE);
	}

}
