package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import models.Abbreviation;
import models.Dictionary;
import models.Event;
import models.EventAnalysis;
import models.Tweet;

import org.bson.types.ObjectId;

import play.Logger;
import play.cache.Cache;
import utils.PLNUtils;
import utils.SendMail;

import com.google.code.morphia.Key;

import dao.EventAnalysisDAO;
import dao.TweetDAO;
import enums.SentimentEnum;

public class AnalyseEventService implements Runnable {

	private List<String> considerWhat;
	private float correctRate;
	private Dictionary dictionary;
	private Event event;
	private String email;
	private Date startDate;
	private Date finishDate;
	

	public AnalyseEventService(Event event, 
			Dictionary dictionary, float correctRate, List<String> considerWhat) {
		
		this.event = event;
		this.dictionary = dictionary;
		this.correctRate = correctRate;
		this.considerWhat = considerWhat;
		
	}

	public AnalyseEventService(Event event, Dictionary dictionary,
			float correctRate, List<String> considerWhat, String email, Date startDate, Date finishDate) {
		
		this.event = event;
		this.dictionary = dictionary;
		this.correctRate = correctRate;
		this.considerWhat = considerWhat;
		this.email = email;
		this.startDate = startDate;
		this.finishDate = finishDate;
	}

	@Override
	public void run() {
		
		// starts the clock to generate  ellapsedtime
		long initialTime = System.currentTimeMillis();
		
		boolean considerHashtag = considerWhat.contains("hashtags");
		boolean considerUser = considerWhat.contains("users");
		boolean considerUrl = considerWhat.contains("urls");
		boolean considerSigla = considerWhat.contains("siglas");
		
		TweetDAO tweetDAO = new TweetDAO();
		long totalTweets = tweetDAO.countAll(event.getId(), startDate, finishDate);
		System.out.println(startDate+" - "+finishDate);
		System.out.println("Total Tweets: "+totalTweets);
		
		EventAnalysisDAO analysisDAO = new EventAnalysisDAO();
		
		// Verificar se ja existe uma analise com os mesmos parametros
		EventAnalysis existentAnalysis = analysisDAO.isThisEventAlreadyBeenAnalysedWithThisParams(event.getId(), dictionary.getId(),
				totalTweets, correctRate, considerHashtag, considerUser, considerUrl, considerSigla, startDate, finishDate);
		if (existentAnalysis == null) {
			
			Logger.info("Não existe analise.. vamos fazer!");
			
			/*
			 * filtrar tweets 'corretos'
			 */
			
			// save the actual analyse to db

			EventAnalysis eventAnalysis = new EventAnalysis(
					new Key<Event>(Event.class, event.getId()),
					new Key<Dictionary>(Dictionary.class, dictionary.getId()),
					dictionary.getName(),
					(int)totalTweets,
					correctRate,
					considerSigla,
					considerHashtag,
					considerUser,
					considerUrl,
					startDate,
					finishDate);
			
			try {
				analysisDAO.save(eventAnalysis);
				
				
				// carregar tweets do banco
				int LIMIT = 2000;
				
				int totalPositives = 0;
				int totalNegatives = 0;
				int totalNeutral = 0;
				int totalIncorrect = 0;
				
				List<Tweet> tweetsToBeUpdated = new ArrayList<Tweet>();
				
				for (int offset=0; offset < totalTweets; offset+=LIMIT) {
					
					List<Tweet> list = tweetDAO.getTweets(event.getId(), LIMIT, offset, startDate, finishDate);
					for (Tweet t : list) {
						
						// Analisar e classificar cada tweet como 
						// INCORRETO, POSITIVO, NEGATIVO, NEUTRO
						@SuppressWarnings("unchecked")
						SentimentEnum analysisResult = PLNUtils.analyseTweet(t.getText(), 
								(HashSet<String>)Cache.get(dictionary.getId().toString()), (List<Abbreviation>)Cache.get("abbreviations"), correctRate, considerHashtag,
								considerUser, considerUrl, considerSigla);
						
						if (analysisResult.equals(SentimentEnum.INCORRECT)) {
							totalIncorrect++;
						} else if(analysisResult.equals(SentimentEnum.NEUTRAL)) {
							totalNeutral++;
						} else if(analysisResult.equals(SentimentEnum.POSITIVE)) {
							totalPositives++;
						} else {
							totalNegatives++;
						}
						
						if (t.getEventAnalysis() == null) {
							HashMap<ObjectId,SentimentEnum> mapAnalysis = new HashMap<ObjectId, SentimentEnum>();
							mapAnalysis.put(eventAnalysis.getId(), analysisResult);
							t.setEventAnalysis(mapAnalysis);
						} else {
							t.getEventAnalysis().put(eventAnalysis.getId(),analysisResult);
						}
						
						tweetsToBeUpdated.add(t);
						
						if (offset < totalTweets) {
							if (tweetsToBeUpdated.size() == LIMIT) {
								if (offset % 10000 == 0 && offset != 0) {
									Logger.info(offset+" tweets from '"+event.getName()+"' updated until now");
								}
								tweetDAO.saveCollection(tweetsToBeUpdated);
								tweetsToBeUpdated.clear();
							}
						} else {
							if (tweetsToBeUpdated.size() == list.size()) {
								Logger.info("Updating the last "+list.size()+" tweets from '"+event.getName()+"'");
								tweetDAO.saveCollection(tweetsToBeUpdated);
								tweetsToBeUpdated.clear();
							}
						}
						
					} // fim for interno
					
				} // fim for externo
				Logger.info("Atualizando "+tweetsToBeUpdated.size()+" tweets");
				tweetDAO.saveCollection(tweetsToBeUpdated);
				
				long ellapsedTime = System.currentTimeMillis() - initialTime;
				
				// atualizar eventAnalysis com o total de tweets positivos/negativos/neutros/incorretos
				eventAnalysis.setTotalIncorrect(totalIncorrect);
				eventAnalysis.setTotalNegatives(totalNegatives);
				eventAnalysis.setTotalNeutral(totalNeutral);
				eventAnalysis.setTotalPositives(totalPositives);
				eventAnalysis.setEllapsedTime(ellapsedTime);
				
				analysisDAO.save(eventAnalysis);
				Logger.info("Analysis finished in "+ellapsedTime+" ms");
				
				// Send email to notify user
				if (!email.isEmpty()) {
					SendMail.sendNotifyEventFinishedTo(email.toLowerCase(), eventAnalysis);
				}
				
			} catch(Exception e) {
				Logger.info("Algum erro ocorreu");
				Logger.error(e.toString());
				analysisDAO.delete(eventAnalysis);
				Logger.info("Rollback analysis..!");
			}
			
			
		} else {
			
			if (!email.isEmpty()) {
				SendMail.sendNotifyEventFinishedTo(email.toLowerCase(), existentAnalysis);
			}
			Logger.info("Analise ja foi feita!");
		}	
			
	}

}
