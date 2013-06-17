package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import models.Abbreviation;
import models.Dictionary;
import models.Event;
import models.Tweet;
import models.Word;
import play.Logger;
import play.cache.Cache;
import play.libs.Json;
import system.ReturnToView;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.AbbreviationDAO;
import dao.TweetDAO;
import dao.WordDAO;

public class GetTweetsService implements Runnable {

	private List<String> considerWhat;
	private float correctRate;
	private Dictionary dictionary;
	private boolean isRecoverAll;
	private Event event;
	private Date startDate;
	private Date finishDate;

	public GetTweetsService(Event event, Date startDate, Date finishDate, boolean isRecoverAll,
			Dictionary dictionary, float correctRate,List<String> considerWhat) {
		
		this.event = event;
		this.isRecoverAll = isRecoverAll;
		this.dictionary = dictionary;
		this.correctRate = correctRate;
		this.considerWhat = considerWhat;
		this.startDate = startDate;
		this.finishDate = finishDate;
	}

	@Override
	public void run() {
		
		ReturnToView vo = new ReturnToView();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("isRecoverAll", isRecoverAll);
		vo.setMessage("Starting");
		vo.setMap(mapa);
		
		Cache.set("getTweetStatus", vo);
		
		long inicio = System.currentTimeMillis();
		
		try {
			File file = new File(PLNUtils.clearString(event.getName())+System.currentTimeMillis()+".json");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			
			TweetDAO dao = new TweetDAO();
			
			long totalTweets = dao.countAllInInterval(event.getId(), startDate, finishDate);
		
			
			vo = (ReturnToView) Cache.get("getTweetStatus");
			
			vo.setMessage("Getting tweets from db...");
			vo.getMap().put("totalTweets", totalTweets);
			
			Cache.set("getTweetStatus", vo);
			
			int LIMIT = 3000;
		
			if (isRecoverAll) {
				// recuperar tudo
				
				List<String> texts = new ArrayList<String>();
				for (int offset=0; offset < totalTweets; offset+=LIMIT) {
					
					List<Tweet> list = dao.getTweetsFromInterval(event.getId(), startDate, finishDate, LIMIT, offset);
					for (Tweet t : list) {
						System.out.println(t.getCreatedAt());
						texts.add(t.getText());
					}
				}
				out.write(Json.toJson(texts).toString());		
				
			} else {
				// é para recuperar apenas tweets corretos
				//TODO: melhorar perfomance
				
				boolean considerHashtags = considerWhat.contains("hashtags");
				boolean considerURLs = considerWhat.contains("urls");
				boolean considerUSERs = considerWhat.contains("users");
				boolean considerSIGLAs = considerWhat.contains("siglas");
	
				/*
				 * Carregar a lista de 'siglas'
				 */
				AbbreviationDAO abbreviationDAO = new AbbreviationDAO();
				List<Abbreviation> abbreviations = abbreviationDAO.listAll();
				
				
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
				
				for (int i=0; i < totalTweets; i+=LIMIT) {
					
					List<Tweet> list = dao.createQuery().filter("event", new Key<Event>(Event.class, event.getId())).limit(LIMIT).offset(i).retrievedFields(true, "text").asList();
					for (Tweet t : list) {
						// para cada tweet, verificar a taxa de palavras corretas
						float rate = PLNUtils.getCorrectRate(t.getText(), dictionaryWords, abbreviations, considerHashtags, considerURLs, considerUSERs, considerSIGLAs);
						if (rate*100 >= correctRate) {
							correctTweets.add(t.getText());		
						}
					}
				}
				out.write(Json.toJson(correctTweets).toString()+"\n");					
				
				Logger.info("Analysis over in "+(System.currentTimeMillis() - inicio)+" ms");
				
				System.out.println(correctTweets.size());
				System.out.println(event.getNrTweets());
				
				float correctRateOverAll =  correctTweets.size() * 100 / event.getNrTweets();
				

				vo = (ReturnToView) Cache.get("getTweetStatus");
				
				vo.getMap().put("totalCorrectTweets", correctTweets.size());
				vo.getMap().put("correctRateOverAll", correctRateOverAll);
				
				Cache.set("getTweetStatus", vo);
				
			}
			
			out.flush();
			out.close();
			
			vo = (ReturnToView) Cache.get("getTweetStatus");
			
			vo.setMessage("File is ready");
			vo.getMap().put("filename", file.getName());
			
			Cache.set("getTweetStatus", vo);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
