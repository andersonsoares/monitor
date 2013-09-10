package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Event;
import models.Tweet;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import dao.base.BaseDAO;
import enums.SentimentEnum;

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
	
	public long countAll(ObjectId eventId, Date startDate, Date finishDate) {
		return createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.field("createdAt").greaterThanOrEq(startDate)
				.field("createdAt").lessThanOrEq(finishDate)
				.countAll();
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
	public List<Tweet> getTweets(ObjectId eventId, int limit, int offset, Date startDate, Date finishDate) {
		return createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.field("createdAt").greaterThanOrEq(startDate)
				.field("createdAt").lessThanOrEq(finishDate)
				.limit(limit)
				.offset(offset)
				.asList();
	}

	/**
	 * Method that count how many different users has tweeted to the respective event
	 * @param _eventId
	 * @return
	 */
	public long countTotalDiffUsers(ObjectId _eventId) {
		
		DBObject match = new BasicDBObject("$match", new BasicDBObject("event.$id",  _eventId ) );
		
		DBObject groupFieldUserId = new BasicDBObject( "_id" , "$twitterUserId");
		DBObject groupUserId = new BasicDBObject("$group", groupFieldUserId);
		
		DBObject groupFieldCount = new BasicDBObject( "_id" , 1);
		groupFieldCount.put("count", new BasicDBObject( "$sum", 1));
		
		DBObject groupCount = new BasicDBObject("$group", groupFieldCount);
		
		
		AggregationOutput output = getCollection().aggregate(match, groupUserId, groupCount);
		for (DBObject res : output.results()) {
			return Long.parseLong(res.get("count").toString());
		}
		
		return 0;
	}

	/**
	 * Method to recover tweets by event and eventAnalysis
	 * sort by createdAt
	 * and recives page and pageLength to paginate
	 * @param eventId
	 * @param eventAnalysisId
	 * @param page
	 * @param pageLength
	 * @return
	 */
	public List<Tweet> getTweetsAfterAnalysedBy(ObjectId eventId, ObjectId eventAnalysisId,
			int page, int pageLength) {
		
		List<Tweet> tweetsList = createQuery()
			.filter("event", new Key<Event>(Event.class, eventId))
			.field("eventAnalysis."+eventAnalysisId.toString()).exists()
			.limit(pageLength)
			.offset(page*pageLength)
			.order("-createdAt")
			.asList();
		
		
		return tweetsList;
	}

	/**
	 * Method that recover tweets by event and eventAnalysis that matches with
	 * SentimentEnum passed as param
	 * order by createdAt
	 * and receives page and pageLength to paginate the result
	 * @param eventId
	 * @param eventAnalysisId
	 * @param sentiment
	 * @param page
	 * @param pageLength
	 * @return
	 */
	public List<Tweet> getTweetsAfterAnalysedByWithSentiment(ObjectId eventId,
			ObjectId eventAnalysisId, SentimentEnum sentiment, int page, int pageLength) {
		
		List<Tweet> tweetsList = createQuery()
			.filter("event", new Key<Event>(Event.class, eventId))
			.filter("eventAnalysis."+eventAnalysisId.toString(), sentiment)
			.limit(pageLength)
			.offset(page*pageLength)
			.order("-createdAt")
			.asList();
		
		return tweetsList;
	}

}
