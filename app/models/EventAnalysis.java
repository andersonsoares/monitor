package models;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/**
 * Class that represents analysis over an event
 * @author aers
 *
 */

@Entity("eventanalysis")
public class EventAnalysis {
	
	@Id
	private ObjectId id;
	
	@Indexed
	private Key<Event> event;
	
	/*
	 * attributes that will be used to generate the analysis
	 */
	@Indexed
	private int totalTweetsAnalysed;
	@Indexed
	private float correctRate;
	@Indexed
	private Key<Dictionary> dictionary;
	private String dictionaryName;
	@Indexed
	private boolean considerSigla;
	@Indexed
	private boolean considerHashtag;
	@Indexed
	private boolean considerUser;
	@Indexed
	private boolean considerUrl;
	
	private Date startDateInterval;
	private Date finishDateInterval;
	
	private Date createdAt;
	// Analysis duration in milliseconds
	private long ellapsedTime;
	
	private int totalPositives;
	
	private int totalNegatives;
	
	private int totalNeutral;
	
	private int totalIncorrect;
	
	// constructors
	public EventAnalysis() {
	}

	public EventAnalysis(Key<Event> event, Key<Dictionary> dictionary, String dictionaryName,
			int totalTweetsAnalysed, float correctRate, boolean considerSigla,
			boolean considerHashtag, boolean considerUser, boolean considerUrl,
			int totalPositives, int totalNegatives,
			int totalNeutral, int totalIncorrect, long ellapsedTime) {
		super();
		this.event 					= event;
		this.totalTweetsAnalysed 	= totalTweetsAnalysed;
		this.correctRate 			= correctRate;
		this.dictionary 			= dictionary;
		this.dictionaryName 		= dictionaryName;
		this.considerSigla 			= considerSigla;
		this.considerHashtag 		= considerHashtag;
		this.considerUser 			= considerUser;
		this.considerUrl 			= considerUrl;
		this.createdAt 				= new Date();
		this.totalPositives 		= totalPositives;
		this.totalNegatives 		= totalNegatives;
		this.totalNeutral 			= totalNeutral;
		this.totalIncorrect 		= totalIncorrect;
		this.ellapsedTime 			= ellapsedTime;
	}
	
	public EventAnalysis(Key<Event> event, Key<Dictionary> dictionary, String dictionaryName,
			int totalTweetsAnalysed, float correctRate, boolean considerSigla,
			boolean considerHashtag, boolean considerUser, boolean considerUrl,
			int totalPositives, int totalNegatives,
			int totalNeutral, int totalIncorrect, long ellapsedTime, Date startDateInterval,
			Date finishDateInterval) {
		super();
		this.event 					= event;
		this.totalTweetsAnalysed 	= totalTweetsAnalysed;
		this.correctRate 			= correctRate;
		this.dictionary 			= dictionary;
		this.dictionaryName 		= dictionaryName;
		this.considerSigla 			= considerSigla;
		this.considerHashtag 		= considerHashtag;
		this.considerUser 			= considerUser;
		this.considerUrl 			= considerUrl;
		this.createdAt 				= new Date();
		this.totalPositives 		= totalPositives;
		this.totalNegatives 		= totalNegatives;
		this.totalNeutral 			= totalNeutral;
		this.totalIncorrect 		= totalIncorrect;
		this.ellapsedTime 			= ellapsedTime;
		this.startDateInterval 		= startDateInterval;
		this.finishDateInterval 	= finishDateInterval;
	}

	// getters & setters;;
	
	public EventAnalysis(Key<Event> event, Key<Dictionary> dictionary, String dictionaryName, int totalTweetsAnalysed,
			float correctRate, boolean considerSigla,
			boolean considerHashtag, boolean considerUser,
			boolean considerUrl, Date startDateInterval, Date finishDateInterval) {
		this.event 					= event;
		this.totalTweetsAnalysed 	= totalTweetsAnalysed;
		this.correctRate 			= correctRate;
		this.dictionary 			= dictionary;
		this.dictionaryName			= dictionaryName;
		this.considerSigla 			= considerSigla;
		this.considerHashtag 		= considerHashtag;
		this.considerUser 			= considerUser;
		this.considerUrl 			= considerUrl;
		this.createdAt 				= new Date();
		this.startDateInterval 		= startDateInterval;
		this.finishDateInterval 	= finishDateInterval;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getTotalTweetsAnalysed() {
		return totalTweetsAnalysed;
	}

	public void setTotalTweetsAnalysed(int totalTweetsAnalysed) {
		this.totalTweetsAnalysed = totalTweetsAnalysed;
	}

	public float getCorrectRate() {
		return correctRate;
	}

	public void setCorrectRate(float correctRate) {
		this.correctRate = correctRate;
	}

	public Key<Dictionary> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Key<Dictionary> dictionary) {
		this.dictionary = dictionary;
	}

	public boolean isConsiderSigla() {
		return considerSigla;
	}

	public void setConsiderSigla(boolean considerSigla) {
		this.considerSigla = considerSigla;
	}

	public boolean isConsiderHashtag() {
		return considerHashtag;
	}

	public void setConsiderHashtag(boolean considerHashtag) {
		this.considerHashtag = considerHashtag;
	}

	public boolean isConsiderUser() {
		return considerUser;
	}

	public void setConsiderUser(boolean considerUser) {
		this.considerUser = considerUser;
	}

	public boolean isConsiderUrl() {
		return considerUrl;
	}

	public void setConsiderUrl(boolean considerUrl) {
		this.considerUrl = considerUrl;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Key<Event> getEvent() {
		return event;
	}

	public void setEvent(Key<Event> event) {
		this.event = event;
	}

	public int getTotalPositives() {
		return totalPositives;
	}

	public void setTotalPositives(int totalPositives) {
		this.totalPositives = totalPositives;
	}

	public int getTotalNegatives() {
		return totalNegatives;
	}

	public void setTotalNegatives(int totalnegatives) {
		this.totalNegatives = totalnegatives;
	}

	public int getTotalNeutral() {
		return totalNeutral;
	}

	public void setTotalNeutral(int totalNeutral) {
		this.totalNeutral = totalNeutral;
	}

	public int getTotalIncorrect() {
		return totalIncorrect;
	}

	public void setTotalIncorrect(int totalIncorrect) {
		this.totalIncorrect = totalIncorrect;
	}

	public long getEllapsedTime() {
		return ellapsedTime;
	}

	public void setEllapsedTime(long ellapsedTime) {
		this.ellapsedTime = ellapsedTime;
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public Date getFinishDateInterval() {
		return finishDateInterval;
	}

	public void setFinishDateInterval(Date finishDateInterval) {
		this.finishDateInterval = finishDateInterval;
	}

	public Date getStartDateInterval() {
		return startDateInterval;
	}

	public void setStartDateInterval(Date startDateInterval) {
		this.startDateInterval = startDateInterval;
	}
	
	

}
