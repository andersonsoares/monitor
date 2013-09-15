package dao;

import java.util.List;

import models.Event;

import org.bson.types.ObjectId;

import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Criteria;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

import dao.base.BaseDAO;
import enums.Situation;

public class EventDAO extends BaseDAO<Event> {
	
	public EventDAO() {
		super(Event.class);
	}

	/**
	 * Return active events which means, events that are currently monitored
	 * @param active
	 * @return
	 */
	public List<Event> listBySituation(Situation... situation) {
		Query<Event> query = createQuery();
		
		Criteria[] criteria = new Criteria[situation.length];
		int size = situation.length;
		for (int i=0; i < size; i++) {
			criteria[i] = query.criteria("situation").equal(situation[i]);
		}
		
		query.or(criteria);
		
		return query.order("-nrTweets").asList();
	}
	
	/**
	 * Method that updates only Situation field of Event
	 * @param eventId
	 * @param active
	 */
	public void setSituation(ObjectId eventId, Situation newSituation) {
		Query<Event> query = ds.createQuery(entityClazz).field(Mapper.ID_KEY).equal(eventId);
		UpdateOperations<Event> ops = ds.createUpdateOperations(entityClazz).set("situation", newSituation);
		ds.update(query, ops);
	}
	
	/**
	 * Methot to update the tweet count for the event
	 * @param eventId
	 * @param new tweet count
	 */
	public void setTweetCount(ObjectId eventId, long tweetCount) {
		Query<Event> query = ds.createQuery(entityClazz).field(Mapper.ID_KEY).equal(eventId);
		UpdateOperations<Event> ops = ds.createUpdateOperations(entityClazz).set("nrTweets", tweetCount);
		ds.update(query, ops);
	}

	public void updateTweetsCountFromAllTweets() {
		
		List<Event> events = listAll();
		
		Query<Event> query = null;
		UpdateOperations<Event> ops = null;
		TweetDAO tweetDAO = new TweetDAO();
		for (Event event : events) {
			query = createQuery().
					field(Mapper.ID_KEY).equal(event.getId());
			ops = createUpdateOperations()
					.set("nrTweets", tweetDAO.countAll(event.getId()));
			ds.update(query, ops);
		}
		
	}
}
