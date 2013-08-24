package dao;

import models.Dictionary;
import models.Event;
import models.EventAnalysis;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

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
	public boolean isThisEventAlreadyBeenAnalysedWithThisParams(ObjectId eventId,
			ObjectId dictionaryId, long totalTweets, float correctRate, boolean considerHashtag,
			boolean considerUser, boolean considerUrl, boolean considerSigla) {
		
		EventAnalysis eventAnalysis = createQuery()
		.filter("event", 				new Key<Event>(Event.class, eventId))
		.filter("dictionary", 			new Key<Dictionary>(Dictionary.class, dictionaryId))
		.filter("totalTweetsAnalysed", 	totalTweets)
		.filter("correctRate", 			correctRate)
		.filter("considerHashtag", 		considerHashtag)
		.filter("considerUser", 		considerUser)
		.filter("considerUrl", 			considerUrl)
		.filter("considerSigla", 		considerSigla)
		.get();
		
		if (eventAnalysis != null) 
			return true;
		return false;
	}

	
	
}
