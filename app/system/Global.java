package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import listeners.TwitterStatusListener;
import listeners.UserStreamListener;
import models.Event;
import models.Tweet;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.cache.Cache;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import actors.EventMonitorActor;
import actors.UpdateTweetCountOnEventsActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.code.morphia.validation.MorphiaValidation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;

import dao.TweetDAO;
import enums.TypeEnum;

/**
 * Class that will setup some tasks before
 * the application starts up
 * 
 * @author Anderson Soares <aersandersonsoares@gmail.com>
 */
public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		
		startGlobalVars(app);
		
		createMongoDbConnection(app);
		
		createTwitterStreams();
		
		createCache();
		
		startSchedulers();
	   
	}
	
	
	private void startGlobalVars(Application app) {
		
		Constants.CACHE_MAX_TWEETS 		= app.configuration().getInt("cache.maxtweets");
		Constants.CACHE_MAX_USERTWEETS 	= app.configuration().getInt("cache.maxusertweets");
		Constants.AKKA_START_IN 	= app.configuration().getInt("akka.start.in");
		Constants.AKKA_LOOP_IN 	= app.configuration().getInt("akka.loop.in");
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onStop(Application app) {
	
		Logger.info("Stopping application");
		List<Tweet> tweets = (List<Tweet>) Cache.get("tweets");
		if (tweets != null && !tweets.isEmpty()) {
			TweetDAO dao = new TweetDAO();
			
			dao.saveCollection(tweets);
			Logger.info("Saving lasts tweets on cache");
		}
		
		tweets = (List<Tweet>) Cache.get("userTweets");
		if (tweets != null && !tweets.isEmpty()) {
			TweetDAO dao = new TweetDAO();
			
			dao.saveCollection(tweets);
			Logger.info("Saving lasts user tweets on cache");
		}
		
		super.onStop(app);
	}

	private void createTwitterStreams() {
		try {

			// Configurando conta um - aersteste - monitor keywords
			Singletons.twitterStream = new TwitterStreamFactory().getInstance();
		    Singletons.twitterStream.setOAuthConsumer(CONSUMER_KEY1, CONSUMER_SECRET1);
		    Singletons.twitterStream.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN1, ACCESS_TOKEN_SECRET1));
		    Singletons.twitterStatusListener = new TwitterStatusListener();
		    Singletons.twitterStream.addListener(Singletons.twitterStatusListener);
		    
		    
		    Singletons.userStream = new TwitterStreamFactory().getInstance();
		    Singletons.userStream.setOAuthConsumer(CONSUMER_KEY2, CONSUMER_SECRET2);
		    Singletons.userStream.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN2, ACCESS_TOKEN_SECRET2));
		    Singletons.userListener = new UserStreamListener();
		    Singletons.userStream.addListener(Singletons.userListener);
		    
			// UserStream(aersmonitor) just need to run once
//			Singletons.userStream.user();
			
			
			Singletons.twitter = TwitterFactory.getSingleton();
			Singletons.twitter.setOAuthConsumer(CONSUMER_KEY2, CONSUMER_SECRET2);
			Singletons.twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN2,ACCESS_TOKEN_SECRET2));
		} catch(Exception e) {
			Logger.error(e.getMessage()); 
		}
	}

	private void createCache() {
		// Create Map with Keywords to the Cache
		Cache.set("keywordMap", new HashMap<Event,Map<String,TypeEnum>>());
		// Create List with tweets to be storaged in Cache before save on DB
		Cache.set("tweets", new ArrayList<Tweet>());
		Cache.set("userTweets", new ArrayList<Tweet>());
		
	}

	private void createMongoDbConnection(Application app) {
		
		Logger.info("Trying to connect with mongo database"); 
		
		
		try {
			
			Singletons.mongo = new MongoClient("127.0.0.1", 27017);
			
			 
			Singletons.morphia = new Morphia();
			
			Singletons.morphia.map(Event.class);
			Singletons.morphia.map(Tweet.class);
			
			Singletons.validation = new MorphiaValidation();
			Singletons.validation.applyTo(Singletons.morphia);
			
			
			
			String userDb = app.configuration().getString("mongo.user");
			
			char[] passDb = app.configuration().getString("mongo.pass").toCharArray();
			
			Singletons.datastore = Singletons.morphia.createDatastore(Singletons.mongo, "monitor", userDb, passDb);
			
			Singletons.datastore.ensureIndexes();   
			Singletons.datastore.ensureCaps();  
			
			
			Logger.info("Connected to MongoDB [" + Singletons.mongo.debugString() + "] database [" + Singletons.datastore.getDB().getName() + "]");
			
			
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}

	/**
	 * Overrides getControllerInstance method to allow inject
	 * dependencies. Note that controllers that will get injected
	 * dependencies must be anottated with @ in 'routes' file
	 * and the controller methods must not be static!
	 */
	@Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
		return INJECTOR.getInstance(controllerClass);
    }
	

	/**
	 * Creates the Guice Injector Singleton
	 * @return injector
	 */
	private static Injector createInjector() {
        return Guice.createInjector();
	}
	

	private void startSchedulers() {
		ActorRef eventMonitorActor = Akka.system().actorOf(new Props(EventMonitorActor.class));
		
		Akka.system().scheduler()
			.schedule(
				Duration.create(Constants.AKKA_START_IN, TimeUnit.SECONDS),
				Duration.create(Constants.AKKA_LOOP_IN, TimeUnit.SECONDS),
				eventMonitorActor,
				"null",
				Akka.system().dispatcher()
			);
		
		ActorRef updateTweetCountOnEventsActor = Akka.system().actorOf(new Props(UpdateTweetCountOnEventsActor.class));
		
		Akka.system().scheduler()
			.schedule(
				Duration.create(60, TimeUnit.SECONDS),
				Duration.create(60, TimeUnit.SECONDS),
				updateTweetCountOnEventsActor,
				"null",
				Akka.system().dispatcher()
			);
	}
	
	private static String CONSUMER_KEY1 = "2Kvke6rMIvMwLhBACfnvjw";
	private static final String CONSUMER_SECRET1 = "uNdKMLaHwU2QIqDcp5m0fhG8MvvSdTEb8s6OGQmDs0";
	private static final String ACCESS_TOKEN1 = "728282665-qRaW39nLguHdVJAnHnKlbYSnrN6Hyv1iOkk99Y";
	private static final String ACCESS_TOKEN_SECRET1 = "9fbfS9FtPkRfAFUheW7Mep5shw4fJT83OBxvsZHbs";

	private static String CONSUMER_KEY2 = "1fUyBflftkkW2zsxspwDQ";
	private static final String CONSUMER_SECRET2 = "STyruGMShCt3nAwBaITT1N3iute7bXIo3TvUTLsyxCw";
	private static final String ACCESS_TOKEN2 = "1366473476-cbXy2moLqFComM1o2pGt1oneWMfLLfjMmgSXxPL";
	private static final String ACCESS_TOKEN_SECRET2 = "DzpRIUPT1Rp9ovMkwaRGBGBPQOnVX53VgNjULcNy8";
	
	// Creating a Guice Injector for Dependency Injection
	private static final Injector INJECTOR = createInjector(); 

	static {
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
    }

}
