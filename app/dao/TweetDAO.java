package dao;

import java.util.ArrayList;
import java.util.List;

import models.Event;
import models.Tweet;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import dao.base.BaseDAO;

public class TweetDAO extends BaseDAO<Tweet> {

	public TweetDAO() {
		super(Tweet.class);
	}

	public long countTweetsFromEvents(List<ObjectId> eventsList) {
		
		List<Key<Event>> eventsKeys = new ArrayList<Key<Event>>();
		
		for (ObjectId objectId : eventsList) {
			eventsKeys.add(new Key<>(Event.class, objectId));
		}
		
		return count(createQuery().field("event").in(eventsKeys));
		
	}
	
	public List<Tweet> listAllFrom(List<ObjectId> eventsList, int limit, int offset) {
		
		List<Key<Event>> eventsKeys = new ArrayList<Key<Event>>();
		
		for (ObjectId objectId : eventsList) {
			eventsKeys.add(new Key<>(Event.class, objectId));
		}
		
		List<Tweet> tweetsList = createQuery().field("event").in(eventsKeys).limit(limit).offset(offset).asList();
		
		return tweetsList;
	}

}
