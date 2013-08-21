package scripts;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import models.Abbreviation;
import models.Event;
import models.Root;
import models.Tweet;
import utils.PLNUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class CreateAbbreviationsFromTweets {
	
	private static Morphia morphiaLocal;
	private static MongoClient mongoLocal;
	private static Datastore datastoreLocal;
	
	public static void main(String[] args) {
		
		
		HashSet<String> dictionary = PLNUtils.readDictionary(false);
		
		
		System.out.println("Iniciando conexoes com os bancos");
		
		// create connection with my local db
		connectToLocalMongo();
		
		
		// iterar sobre todos os tweets! :/
		
//		TweetDAO dao = new TweetDAO();
		long totalTweets = datastoreLocal.getCount(Tweet.class);
		
		int LIMIT = 3000;
		ArrayList<String> tweets = new ArrayList<String>();
		for (int i=0; i < totalTweets; i+=LIMIT) {
			
			List<Tweet> list = datastoreLocal.createQuery(Tweet.class).limit(LIMIT).offset(i-1).retrievedFields(true, "text").asList();
			for (Tweet t : list) {
				tweets.add(t.getText());
			}
		}
		
		System.out.println(tweets.size());
		
		HashSet<Abbreviation> abreviacoes = new HashSet<Abbreviation>();
		
		for (String tweet : tweets) {
			HashSet<Abbreviation> siglas = PLNUtils.getAbreviacoes(tweet, dictionary);
			for (Abbreviation sigla : siglas) {
				if (!abreviacoes.contains(sigla)) {
					abreviacoes.add(sigla);
				}
			}
		}
		
		
		for (Abbreviation abbreviation : abreviacoes) {
			System.out.println(abbreviation.toString());
		}
		
		datastoreLocal.save(abreviacoes);
		System.out.println("Siglas foram salvas");
		
		
		
		
	}
	
	
	
	private static void connectToLocalMongo() {
		/*
		 * LOCAL CONF
		 */
		String userDb = 	"monitor";
		char[] passDb = 	"monitor123".toCharArray();
		String hostDb = 	"li216-187.members.linode.com";
//		String hostDb = 	"127.0.0.1";
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
