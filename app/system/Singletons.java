package system;

import listeners.UserStreamListener;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterStream;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.validation.MorphiaValidation;
import com.mongodb.MongoClient;

/**
 * Class that will have singleton instances of morphia/datastore/mongo/validation
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class Singletons {
	static public MongoClient mongo;
	static public MorphiaValidation validation;
	static public Morphia morphia;
	static public Datastore datastore;
	static public Twitter twitter;
	static public TwitterStream twitterStream;
	static public TwitterStream userStream;
	public static StatusListener twitterStatusListener;
	public static UserStreamListener userListener;
}