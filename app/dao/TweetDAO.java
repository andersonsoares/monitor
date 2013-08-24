package dao;

import java.util.ArrayList;
import java.util.Date;
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
			eventsKeys.add(new Key<Event>(Event.class, objectId));
		}
		
		return count(createQuery().field("event").in(eventsKeys));
		
	}
	
	public List<Tweet> listAllFrom(List<ObjectId> eventsList, int limit, int offset) {
		
		List<Key<Event>> eventsKeys = new ArrayList<Key<Event>>();
		
		for (ObjectId objectId : eventsList) {
			eventsKeys.add(new Key<Event>(Event.class, objectId));
		}
		
		List<Tweet> tweetsList = createQuery().field("event").in(eventsKeys).limit(limit).offset(offset).asList();
		
		return tweetsList;
	}

	public long countAll(ObjectId eventId) {
		return createQuery().filter("event", new Key<Event>(Event.class, eventId)).countAll();
	}
	
	public long countAllInInterval(ObjectId id, Date startDate, Date finishDate) {
		return createQuery()
				.filter("event", new Key<Event>(Event.class, id))
				.field("createdAt").greaterThanOrEq(startDate)
				.field("createdAt").lessThanOrEq(finishDate)
				.countAll();
	}

	public long countInInterval(ObjectId _eventId, Date startDate,
			Date finishDate) {
		Key<Event> eventKey = new Key<Event>(Event.class, _eventId);
		
		return createQuery()
				.filter("event", eventKey)
				.field("createdAt").greaterThanOrEq(startDate)
				.field("createdAt").lessThanOrEq(finishDate)
				.countAll();
	}

	public List<Tweet> getTweetsFromInterval(ObjectId eventId, Date startDate,
			Date finishDate, int limit, int offset) {
		return createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.field("createdAt").greaterThanOrEq(startDate)
				.field("createdAt").lessThanOrEq(finishDate)
				.limit(limit)
				.offset(offset)
				.retrievedFields(true, "text")
				.asList();
	}

	public List<Tweet> getTweets(ObjectId eventId, int limit, int offset) {
		return createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.limit(limit)
				.offset(offset)
				.asList();
	}

}
