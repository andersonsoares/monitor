package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Event;
import models.Root;
import models.Tweet;

import org.bson.types.ObjectId;

import play.Logger;
import ptstemmer.Stemmer.StemmerType;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.RootDAO;
import dao.TweetDAO;

public class RootService {

	/**
	 * Method that will receive eventsList and cutValue
	 * Recover all tweets from this events and analyse each word of them
	 * to create a DB with all its roots =O
	 * @param eventsList
	 * @param cutValue
	 */
	public void generate(List<ObjectId> eventsList, int cutValue, StemmerType algoritm) {
		
		int LIMIT = 800;
		
		TweetDAO tweetDAO = new TweetDAO();
		RootDAO rootDAO = new RootDAO();
		
		// Removing all
		rootDAO.drop();
		
		long startTime = System.currentTimeMillis();
		
		HashMap<String,Root> roots = new HashMap<String,Root>();
		
		for (ObjectId eventId : eventsList) {
			
			long totalTweets = tweetDAO.createQuery().filter("event", new Key<Event>(Event.class, eventId)).countAll();
			
			for (int i=0; i < totalTweets; i+=LIMIT) {

				List<Tweet> list = tweetDAO.createQuery().filter("event", new Key<Event>(Event.class, eventId)).limit(LIMIT).offset(i).retrievedFields(true, "text").asList();
				
				
				for (Tweet tweet : list) {
					// TODO: save tweet in the normalizedForm
					String normalizedText = PLNUtils.normalizedTweet(tweet.getText());
					
					String[] tokens = normalizedText.split(" ");
					
					for (String tokenGenerator : tokens) {
						
						if (tokenGenerator.length() > 2) {
							String rootWord = PLNUtils.getRoot(tokenGenerator, algoritm);
							if (rootWord != null) {
								// If there is no root, lets add it to the list :)
								if (!roots.containsKey(rootWord)) {
									Root root = new Root(rootWord, tokenGenerator);
									root.setCount(1); // start couting
									roots.put(rootWord, root);
								} else {
									// RootWord is already saved.. lets increase the count, and add
									// generatorToken to it
									Root root = roots.get(rootWord);
									root.setCount(root.getCount() + 1);
									root.getWordsGenerators().add(tokenGenerator);
									
									roots.put(rootWord, root);
								}
								
							}
						}
					}
				}
			}
		}
		
		
		long startTimeToSave = System.currentTimeMillis();
		
		Logger.info("Time to compute: "+(startTimeToSave-startTime)+" ms");
		
		ArrayList<Root> rootsToSave = new ArrayList<Root>();
		int i = 0;
		for (String rootWord : roots.keySet()) {
		
			Root root = roots.get(rootWord);
			
			// Verifies if the rootWord will be used
			if (root.getCount() < cutValue) {
				root.setRemoved(true);
			}
			rootsToSave.add(root);
			
			if (i >= 1000 || (i == (roots.size() - 1))) {
				rootDAO.saveCollection(rootsToSave);
				rootsToSave.clear();
				i = 0;
			}
			
			i++;
		}
		long finishTimeToSave = System.currentTimeMillis();
		Logger.info("Time to save on DB: "+(finishTimeToSave-startTimeToSave)+" ms");
		
		Logger.info("Found total roots: "+roots.size());
		
		long finishTime = System.currentTimeMillis();
		Logger.info("Total time to generate: "+(finishTime-startTime)+" ms");
	}

}
