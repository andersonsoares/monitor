package models;

import java.util.Date;
import java.util.HashMap;

import org.bson.types.ObjectId;

import play.data.validation.Constraints.Required;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

import enums.Situation;
import enums.TypeEnum;

/**
 * Class that represent a world event like world cup, talks etc
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */

@Entity("events")
public class Event {
	
	@Id
	private ObjectId id;
	/**
	 * The event name
	 */
	@Indexed
	private String nameLowerCase;
	@Required
	private String name;
	
	@Indexed
	private Situation situation;
	
	/**
	 * Start and Finish time to monitor
	 */
	private Date startDate;
	private Date finishDate;
	
	/**
	 * Date the event was created
	 */
	private Date createdAt;
	
	
	/**
	 * Keywords to be monitored. Ex: [#google,#android,#brasil,@dilma]
	 */
	private HashMap<String,TypeEnum> keywords;
	
	/**
	 * Total number of tweets
	 */
	private int nrTweets;
	
	// ---------------------------------------------------------------------
	
	// Constructors
		public Event() {
			this.situation = Situation.NEVER_STARTED;
			this.createdAt = new Date(System.currentTimeMillis());
			this.keywords = new HashMap<String, TypeEnum>();
		}
		
		public Event(String name) {
			this.setName(name);
			this.situation = Situation.NEVER_STARTED;
			this.createdAt = new Date(System.currentTimeMillis());
		}

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public String getNameLowerCase() {
			return nameLowerCase;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
			this.nameLowerCase = name.toLowerCase();
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date start) {
			this.startDate = start;
		}

		public Date getFinishDate() {
			return finishDate;
		}

		public void setFinishDate(Date finish) {
			this.finishDate = finish;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public HashMap<String, TypeEnum> getKeywords() {
			return keywords;
		}

		public void setKeywords(HashMap<String, TypeEnum> keywords) {
			this.keywords = keywords;
		}

		public int getNrTweets() {
			return nrTweets;
		}

		public void setNrTweets(int nrTweets) {
			this.nrTweets = nrTweets;
		}

		public Situation getSituation() {
			return situation;
		}

		public void setSituation(Situation situation) {
			this.situation = situation;
		}
		

}
