package models;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity("tweets")
public class Tweet {

	@Id
	private ObjectId id;
	
	@Indexed
	private Key<Event> event;
	
	
	// Tweets default attributes by Twitter
//	@Indexed
	private long tweetId;
	
	private String text;
	
	private long retweet_count;

	private boolean retweeted;
	
	// user author attributes
	private long twitterUserId;
	private String profile_image_url;
	
		
	private boolean normalized;
	private String normalizedText;
	
	private float correctRate;
	
	@Indexed
	private Date createdAt;
	
	// Default Constructor
	public Tweet() {}
	

	// Getters e Setters
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Key<Event> getEvent() {
		return event;
	}

	public void setEvent(Key<Event> event) {
		this.event = event;
	}

	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getRetweet_count() {
		return retweet_count;
	}

	public void setRetweet_count(long retweet_count) {
		this.retweet_count = retweet_count;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public long getTwitterUserId() {
		return twitterUserId;
	}

	public void setTwitterUserId(long twitterUserId) {
		this.twitterUserId = twitterUserId;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public boolean isNormalized() {
		return normalized;
	}

	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}

	public String getNormalizedText() {
		return normalizedText;
	}

	public void setNormalizedText(String normalizedText) {
		this.normalizedText = normalizedText;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public float getCorrectRate() {
		return correctRate;
	}


	public void setCorrectRate(float correctRate) {
		this.correctRate = correctRate;
	}
	
}
