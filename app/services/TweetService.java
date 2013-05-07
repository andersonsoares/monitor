package services;

import java.io.File;
import java.util.List;

import models.Event;
import models.Tweet;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import dao.EventDAO;
import dao.TweetDAO;

public class TweetService {

	
	/**
	 * Function that create a file with with the tweets that refer 
	 * with the eventId passed as param
	 * @return
	 */
	public File createFileWithTweetsFrom(ObjectId eventId) {
		
		EventDAO eventDAO = new EventDAO();
		Event event = eventDAO.findById(eventId);
		
		if (event != null) {
			
			System.out.println("Recuperando tweets from: "+event.getName());
			
			TweetDAO dao = new TweetDAO();
			
			List<Tweet> list = dao.createQuery().filter("event", new Key<Object>(Event.class, event.getId())).asList();
			
			
			
			for (Tweet tweet : list) {
				System.out.println(tweet.getText());
			}
			
			System.out.println("Total: "+list.size());
			
			
			
		} else {
			System.out.println();
		}
		
		
		return null;
	}
	
}
