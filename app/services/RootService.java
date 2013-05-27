package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Acronym;
import models.Event;
import models.Glossary;
import models.Root;
import models.Tweet;

import org.bson.types.ObjectId;

import play.Logger;
import play.cache.Cache;
import system.StatusProgress;
import utils.PLNUtils;

import com.google.code.morphia.Key;

import dao.AcronymDAO;
import dao.GlossaryDAO;
import dao.RootDAO;
import dao.TweetDAO;

public class RootService implements Runnable {

	private final Glossary glossary;
	
	public RootService(Glossary glossary) {
		this.glossary = glossary;
	}

	/**
	 * Method that will receive eventsList and cutValue
	 * Recover all tweets from this events and analyse each word of them
	 * to create a DB with all its roots =O
	 * @param eventsList
	 * @param cutValue
	 */
	@Override
	public void run() {
		
		try {
			
			Cache.set("generateRootProgress", new StatusProgress("started", 0.0f));
			
			GlossaryDAO glossaryDAO = new GlossaryDAO();
			
			Key<Glossary> glossaryKey = glossaryDAO.save(glossary);
			
			// Start total progress for computing the roots generation
			int LIMIT = 1500;
			
			TweetDAO tweetDAO = new TweetDAO();
			RootDAO rootDAO = new RootDAO();
			AcronymDAO acronymDAO = new AcronymDAO();
			
			// Removing all
			rootDAO.drop();
			acronymDAO.drop();
			
			long startTime = System.currentTimeMillis();
			
			HashMap<String,Root> roots = new HashMap<String,Root>();
			HashMap<String, Acronym> acronyms = new HashMap<String, Acronym>();
			
			for (ObjectId eventId : glossary.getEventsList()) {
				
				long totalTweets = tweetDAO.createQuery().filter("event", new Key<Event>(Event.class, eventId)).countAll();
				
				for (int i=0; i < totalTweets; i+=LIMIT) {
	
					long startToFetch = System.currentTimeMillis();
					
					List<Tweet> list = tweetDAO.createQuery().filter("event", new Key<Event>(Event.class, eventId)).limit(LIMIT).offset(i).retrievedFields(true, "text").asList();
					
					
					for (Tweet tweet : list) {
						
						// Get Siglas / Acronyms
						List<String> siglas = PLNUtils.getSiglas(tweet.getText(), glossary.getAlgoritm());
						for (String acronym : siglas) {
							if (!acronyms.containsKey(acronym)) {
								Acronym sigla = new Acronym(glossaryKey, acronym);
								sigla.setCount(1);
								acronyms.put(acronym, sigla);
							} else {
								Acronym sigla = acronyms.get(acronym);
								sigla.setCount(sigla.getCount() + 1);
								acronyms.put(acronym, sigla);
							}
						}
						
						
						// TODO: save tweet in the normalizedForm
						String normalizedText = PLNUtils.normalizedTweet(tweet.getText());
						String[] tokens = normalizedText.split(" ");
						
						for (String tokenGenerator : tokens) {
							
							if (tokenGenerator.length() > 2) {
								String rootWord = PLNUtils.getRoot(tokenGenerator, glossary.getAlgoritm());
								if (rootWord != null) {
									// If there is no root, lets add it to the list :)
									if (!roots.containsKey(rootWord)) {
										Root root = new Root(rootWord, tokenGenerator);
										root.setGlossary(glossaryKey);
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
					
					Logger.info(i+" to "+(i+LIMIT)+": Fetched and computed in: "+(System.currentTimeMillis() - startToFetch));
					float progress = (i + LIMIT) * 100 / totalTweets;
					
					Cache.set("generateRootProgress", new StatusProgress(eventId.toString(), progress));
				}
			}
			
			
			long startTimeToSave = System.currentTimeMillis();
			
			Logger.info("Time to compute: "+(startTimeToSave-startTime)+" ms");
			
			ArrayList<Root> rootsToSave = new ArrayList<Root>();
			int i = 0;
			for (String rootWord : roots.keySet()) {
			
				Root root = roots.get(rootWord);
				
				// Verifies if the rootWord will be used
				if (root.getCount() < glossary.getCutValue()) {
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
			

			
			ArrayList<Acronym> acronymsToSave = new ArrayList<Acronym>();
			i = 0;
			for (String acronym : acronyms.keySet()) {
			
				Acronym sigla = acronyms.get(acronym);
				
				acronymsToSave.add(sigla);
				
				if (i >= 1000 || (i == (acronyms.size() - 1))) {
					acronymDAO.saveCollection(acronymsToSave);
					acronymsToSave.clear();
					i = 0;
				}
				
				i++;
			}
			long finishTimeToSave = System.currentTimeMillis();
			Logger.info("Time to save on DB: "+(finishTimeToSave-startTimeToSave)+" ms");
			
			Logger.info("Found total roots: "+roots.size());
			
			long finishTime = System.currentTimeMillis();
			Logger.info("Total time to generate: "+(finishTime-startTime)+" ms");
			
			Cache.remove("generateRootProgress");
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
			e.printStackTrace();
		}

		
	}

}
