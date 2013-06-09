package scripts;

import java.net.UnknownHostException;
import java.util.List;

import models.Event;
import models.Root;
import models.Tweet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

/**
 * Class that updates my local mongodb database with remote(linode) one
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class UpdateLocalDB {

	private static MongoClient mongoLinode;
	private static Morphia morphiaLinode;
	private static Morphia morphiaLocal;
	private static MongoClient mongoLocal;
	private static Datastore datastoreLocal;
	private static Datastore datastoreLinode;

	public static void main(String[] args) {
		
		System.out.println("Iniciando conexoes com os bancos");
		
		// create connection with my local db
		connectToLocalMongo();
		// create connection with linodes db
		connectToLinodeMongo();
		
		datastoreLocal.getCollection(Event.class).drop();
		datastoreLocal.getCollection(Tweet.class).drop();
		
		int LIMIT = 1500;
		
		List<Event> eventsList = datastoreLinode.find(Event.class).asList();
		
		for (Event event : eventsList) {
			
			System.out.println("Found Event: "+event.getName());
			
			// save the event
			datastoreLocal.save(event);
			
			// load tweets with batches of LIMIT
			
			long totalTweets = datastoreLinode.createQuery(Tweet.class).filter("event", new Key<Event>(Event.class, event.getId())).countAll();
			
			
			for (int i=0; i < totalTweets; i+=LIMIT) {
				
				List<Tweet> list = datastoreLinode.createQuery(Tweet.class).filter("event", new Key<Event>(Event.class, event.getId())).limit(LIMIT).offset(i).asList();
				
				datastoreLocal.save(list);
				
				System.out.println("Saved: "+(LIMIT + i)+" of "+totalTweets);
				
			}
			
			
			
		}
		
	}

	private static void connectToLocalMongo() {
		/*
		 * LOCAL CONF
		 */
		String userDb = 	"monitor";
		char[] passDb = 	"monitor123".toCharArray();
		String hostDb = 	"127.0.0.1";
		int portDb =	 	27017;
		String database = 	"monitor";
		try {
			mongoLocal = new MongoClient(hostDb, portDb);
			morphiaLocal = new Morphia();
			
			morphiaLocal.map(Event.class);
			morphiaLocal.map(Root.class);
			morphiaLocal.map(Tweet.class);
			
			datastoreLocal = morphiaLocal.createDatastore(mongoLocal, database, userDb, passDb);
			
			datastoreLocal.ensureIndexes();   
			datastoreLocal.ensureCaps();
			
			System.out.println("Connected to LOCAL MongoDB [" + mongoLocal.debugString() + "] database [" + datastoreLocal.getDB().getName() + "]");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static void connectToLinodeMongo() {
		
		/*
		 * LINODE CONF
		 */
		String userLinode = 	"monitor";
		char[] passLinode = 	"monitor123".toCharArray();
		String hostLinode = 	"li216-187.members.linode.com";
		int portDb =	 	27017;
		String database = 	"monitor";
		
		try {
			mongoLinode = new MongoClient(hostLinode, portDb);
			morphiaLinode = new Morphia();
			
			morphiaLinode.map(Event.class);
			morphiaLocal.map(Root.class);
			morphiaLinode.map(Tweet.class);
			
			datastoreLinode = morphiaLinode.createDatastore(mongoLinode, database, userLinode, passLinode);
			
			datastoreLinode.ensureIndexes();   
			datastoreLinode.ensureCaps();
			
			System.out.println("Connected to LINODE MongoDB [" + mongoLinode.debugString() + "] database [" + datastoreLinode.getDB().getName() + "]");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	
}
