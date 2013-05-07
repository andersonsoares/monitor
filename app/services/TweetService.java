package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import models.Event;
import models.Tweet;

import org.bson.types.ObjectId;

import play.Logger;
import play.libs.Json;

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
		
		int LIMIT = 800;
		
		if (event != null) {
			
			try {
			
				TweetDAO dao = new TweetDAO();
				
				long totalTweets = dao.createQuery().filter("event", new Key<Event>(Event.class, event.getId())).countAll();
				
				File file = new File(event.getName()+System.currentTimeMillis()+".json");
				
				
				BufferedWriter out = new BufferedWriter(new FileWriter(file), 32768);
				
				
				for (int i=0; i < totalTweets; i+=LIMIT) {
					
					List<Tweet> list = dao.createQuery().filter("event", new Key<Event>(Event.class, event.getId())).limit(LIMIT).offset(i).asList();
					out.write(Json.toJson(list).toString());		
				}
				
				out.flush();
				out.close();
				
				return file;
				
			} catch (IOException e) {
				Logger.error("Error on creating file to download: "+e.getMessage());
			}
			
		} else {
			System.out.println();
		}
		
		
		return null;
	}
	
}
