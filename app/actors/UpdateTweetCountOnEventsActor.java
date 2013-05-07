package actors;

import java.util.HashMap;
import java.util.Set;

import models.Event;
import play.Logger;
import play.cache.Cache;
import akka.actor.UntypedActor;

import com.google.code.morphia.Key;

import dao.EventDAO;
import dao.TweetDAO;
import enums.TypeEnum;

public class UpdateTweetCountOnEventsActor extends UntypedActor {

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object arg0) throws Exception {
		
		boolean has_new_tweets_on_db = (boolean) Cache.get("has_new_tweets_on_db");
		if (has_new_tweets_on_db) {
		
			Logger.info("Updating events twitter count");
			HashMap<Event, HashMap<String, TypeEnum>> keywordMap = (HashMap<Event, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
			Set<Event> eventsOnCache = keywordMap.keySet();
			
			if (eventsOnCache != null && !eventsOnCache.isEmpty()) {
				TweetDAO tweetDao = new TweetDAO();
				EventDAO dao = new EventDAO();
				for (Event event : eventsOnCache) {
					long tweetCount = tweetDao.createQuery().filter("event", new Key<Event>(Event.class, event.getId())).countAll();
					dao.setTweetCount(event.getId(), tweetCount);
				}
				
			}
			
			Cache.set("has_new_tweets_on_db", false);
		}
		
	}

}
