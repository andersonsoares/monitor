package scripts;

import java.net.UnknownHostException;
import java.util.HashSet;

import models.Event;
import models.Root;
import models.Tweet;

import utils.PLNUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class SetCorrectRateOnTweets {
	
	private static MongoClient mongoLinode;
	private static Morphia morphiaLinode;
	private static Morphia morphiaLocal;
	private static MongoClient mongoLocal;
	private static Datastore datastoreLocal;
	private static Datastore datastoreLinode;
	
	public static void main(String[] args) {
		
		
		HashSet<String> dictionary = PLNUtils.readDictionary(false);
		
		
		System.out.println("Iniciando conexoes com os bancos");
		
		// create connection with my local db
		connectToLocalMongo();
		
		
		
		
		
		
	}
	
	
	
	private static void connectToLocalMongo() {
		/*
		 * LOCAL CONF
		 */
		String userDb = 	"aers";
		char[] passDb = 	"aers123".toCharArray();
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


}
