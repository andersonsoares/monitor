package scripts;

import java.util.HashSet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

import utils.PLNUtils;

public class SetCorrectRateOnTweets {
	
	private static MongoClient mongoLinode;
	private static Morphia morphiaLinode;
	private static Morphia morphiaLocal;
	private static MongoClient mongoLocal;
	private static Datastore datastoreLocal;
	private static Datastore datastoreLinode;
	
	public static void main(String[] args) {
		
		String str = "AC: sobrinho de governador tem regalias na prisão, dizem agentes http://t.co/f9YVrvnq60 #TerraPolícia";
		
		str = PLNUtils.removeRT(str);
		str = PLNUtils.replaceURLs(str);
		str = PLNUtils.replaceUSERs(str);
		str = PLNUtils.replaceHASHTAG(str);
		
		str = PLNUtils.removeDigits(str);
		
		str = PLNUtils.removePunctuation(str);
		str = PLNUtils.removeWhiteSpacesNotNecessary(str);
		
		System.out.println(str);
		
//		HashSet<String> dictionary = PLNUtils.readDictionary(false);
		
		
//		System.out.println("Iniciando conexoes com os bancos");
		
		// create connection with my local db
//		connectToLocalMongo();
		
		
		
		
	}
	
	
	public static float getCorrectRate(String tweet) {
		
		String str = tweet;
		
		
		str = PLNUtils.removeRT(str);
		str = PLNUtils.replaceURLs(str);
		str = PLNUtils.replaceUSERs(str);
		str = PLNUtils.replaceHASHTAG(str);
		
		str = PLNUtils.removeDigits(str);
		
		str = PLNUtils.removePunctuation(str);
		str = PLNUtils.removeWhiteSpacesNotNecessary(str);
		
		return 0f;
	}

}
