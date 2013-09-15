package dao;

import java.util.Date;
import java.util.List;

import models.Dictionary;
import models.Event;
import models.EventAnalysis;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;

import dao.base.BaseDAO;

/**
 * Class to persist EventAnalysis
 * 
 * @author aers
 *
 */
public class EventAnalysisDAO extends BaseDAO<EventAnalysis> {

	public EventAnalysisDAO() {
		super(EventAnalysis.class);
	}

	/*
	 * Method that verifies if there is one analysis of event with this SAME 
	 * params
	 */
	public EventAnalysis isThisEventAlreadyBeenAnalysedWithThisParams(ObjectId eventId,
			ObjectId dictionaryId, long totalTweets, float correctRate, boolean considerHashtag,
			boolean considerUser, boolean considerUrl, boolean considerSigla, Date startDateInterval,
			Date finishDateInterval) {
		
		EventAnalysis eventAnalysis = createQuery()
		.filter("event", 				new Key<Event>(Event.class, eventId))
		.filter("dictionary", 			new Key<Dictionary>(Dictionary.class, dictionaryId))
		.filter("totalTweetsAnalysed", 	totalTweets)
		.filter("correctRate", 			correctRate)
		.filter("considerHashtag", 		considerHashtag)
		.filter("considerUser", 		considerUser)
		.filter("considerUrl", 			considerUrl)
		.filter("considerSigla", 		considerSigla)
		.filter("startDateInterval", 	startDateInterval)
		.filter("finishDateInterval",   finishDateInterval)
		.get();
		
		return eventAnalysis;
	}

	/**
	 * Method to retrive last 5 analisis from the event
	 * @param eventId
	 * @param size
	 * @return
	 */
	public List<EventAnalysis> getLast(ObjectId eventId, int size) {
		
		List<EventAnalysis> list = createQuery()
		.filter("event", new Key<Event>(Event.class, eventId))
		.order("totalTweetsAnalysed")
		.order("correctRate")
		.limit(size)
		.asList();
		
		
		return list;
	}

	/**
	 * Method that recover all eventAnalysis from an specific event
	 * @param eventId
	 * @return
	 */
	public List<EventAnalysis> listAllFromEvent(ObjectId eventId) {
		
		List<EventAnalysis> list = createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.order("-createdAt")
				.asList();
		return list;
	}

	/**
	 * Method that recover all eventAnalysis from an specific event
	 * @param eventId
	 * @return
	 */
	public List<EventAnalysis> listAllFromEvent(ObjectId eventId, String orderBy) {
		
		List<EventAnalysis> list = createQuery()
				.filter("event", new Key<Event>(Event.class, eventId))
				.order(orderBy)
				.asList();
		return list;
	}

	/**
	 * Method that counts how many analysis a event have
	 * @param id
	 * @return
	 */
	public long getCountByEvent(ObjectId eventId) {

		return createQuery()
		.filter("event", new Key<Event>(Event.class, eventId))
		.countAll();
	}

	/**
	 * Remove all eventAnalysiss that have eevntId
	 * @param eventId
	 */
	public void removeAllByEvent(ObjectId eventId) {
		Query<EventAnalysis> query = createQuery()
				.filter("event", new Key<Event>(Event.class, eventId));
		deleteByQuery(query);
	}
	
	
}
