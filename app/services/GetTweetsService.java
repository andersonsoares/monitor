package services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import models.Dictionary;
import models.Event;
import models.Tweet;
import models.Word;
import play.Logger;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.TweetDAO;
import dao.WordDAO;

public class GetTweetsService implements Runnable {

	private List<String> considerWhat;
	private float correctRate;
	private Dictionary dictionary;
	private boolean isRecoverAll;
	private Event event;

	public GetTweetsService(Event event, boolean isRecoverAll,
			Dictionary dictionary, float correctRate,List<String> considerWhat) {
		
		this.event = event;
		this.isRecoverAll = isRecoverAll;
		this.dictionary = dictionary;
		this.correctRate = correctRate;
		this.considerWhat = considerWhat;
		
	}

	@Override
	public void run() {
		
		long inicio = System.currentTimeMillis();
		
		if (isRecoverAll) {
			System.out.println("É para recuperar TUDO");
		} else {
			// é para recuperar apenas tweets corretos
			//TODO: melhorar perfomance
			
			boolean considerHashtags = considerWhat.contains("hashtags");
			boolean considerURLs = considerWhat.contains("urls");
			boolean considerUSERs = considerWhat.contains("users");

			/*
			 *  Carregar dicionario para a memoria
			 */
			WordDAO wordDAO = new WordDAO();
			List<Word> words = wordDAO.listByDictionaryId(dictionary.getId());
			HashSet<String> dictionaryWords = new HashSet<String>();
			for (Word word : words) {
				dictionaryWords.add(word.getName());
			}
			Logger.info("Dictionary loaded in "+(System.currentTimeMillis() - inicio)+" ms");
			
			/*
			 * CARREGAR TWEETS DO BANCO
			 */
			
			ArrayList<String> correctTweets = new ArrayList<String>();

			TweetDAO dao = new TweetDAO();
			
			long totalTweets = dao.countAll(event.getId());
			
			int LIMIT = 3000;
			
			for (int i=0; i < totalTweets; i+=LIMIT) {
				
				List<Tweet> list = dao.createQuery().filter("event", new Key<Event>(Event.class, event.getId())).limit(LIMIT).offset(i).retrievedFields(true, "text").asList();
				for (Tweet t : list) {
					// para cada tweet, verificar a taxa de palavras corretas
					float rate = PLNUtils.getCorrectRate(t.getText(), dictionaryWords, considerHashtags, considerURLs, considerUSERs);
					if (rate*100 >= correctRate) {
						correctTweets.add(t.getText());
					}
				}		
			}
			
			Logger.info("Analysis over in "+(System.currentTimeMillis() - inicio)+" ms");
			
			System.out.println(correctTweets.size());
			System.out.println(event.getNrTweets());
			
			float correctRateOverAll =  correctTweets.size() * 100 / event.getNrTweets();
			System.out.println("Correct rate Over all: "+correctRateOverAll);
			
			
			
			
		}
		

		
		
		
		
		
		
		
		
		
		
		

	}

}
