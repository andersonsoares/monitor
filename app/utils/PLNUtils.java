package utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;


/**
 * Class that contains some usefull PLN functions
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class PLNUtils {

	
	public static String replaceURLs(String str) {
		return str.replaceAll("((www\\.[\\s]+)|(https?://[^\\s]+))", "URL");
	}
	
	public static String replaceUSERs(String str) {
		return str.replaceAll("@[^\\s]+", "USER");
	}
	
	public static String removeNonAsciiChars(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;
	}
	
	public static String removePunctuation(String str) {
		return str.replaceAll("\\p{Punct}+", " ");
	}
	
	public static String tryFixRepeatedChars(String str) {
		return str.replaceAll("(([a-z][A-Z]+)\\2{2,})?", "$2");
	}
	
	public static String removeWhiteSpacesNotNecessary(String str) {
		return str.replaceAll("\\s+", " ");
	}
	
	public static String removeRT(String str) {
		return str.replaceAll("RT"," ");
	}
	
	public static String removeURLs(String str) {
		return str.replaceAll("URL", " ");
	}
	
	public static String removeUSERs(String str) {
		return str.replaceAll("USER", " ");
	}
	
	public static String removeDigits(String str) {
		return str.replaceAll("[0-9]", " ");
	}
	
	
	public static List<String> getSiglas(String str) {
		List<String> siglas = new ArrayList<String>();
	
		
		str = removeRT(str);
		str = replaceURLs(str);
		str = replaceUSERs(str);
		str = removeNonAsciiChars(str);
		str = removePunctuation(str);
		str = removeWhiteSpacesNotNecessary(str);
		str = tryFixRepeatedChars(str);
		
		String[] tokens = str.split(" ");
		for (String string : tokens) {
			if (isSigla(string)) {
				siglas.add(string);
			}
		}
		
		return siglas;
	}
	
	public static String normalizedTweet(String str) {
		
		str = str.toLowerCase();
		str = removeRT(str);
		str = replaceURLs(str);
		str = removeURLs(str);
		str = replaceUSERs(str);
		str = removeUSERs(str);
		str = removeDigits(str);
		str = removeNonAsciiChars(str);
		str = removePunctuation(str);
		str = removeWhiteSpacesNotNecessary(str);
		str = tryFixRepeatedChars(str);
		
		return str;
	}
	

	/**
	 * Function that checks if a word could be a 'SIGLA'
	 * Verifies if the word if all UpperCase and then runs
	 * Stemmer to get its root.
	 * @param str
	 * @return
	 */
	public static boolean isSigla(String str) {
		
		if (str.length() >= 2) { 
			if (!str.substring(0,1).matches("[0-9]")) {
				
			
				// Verifies if String is all toUpperCase()
				if (str.equals(str.toUpperCase())) {
					
					if (!str.equals("USER") && !str.equals("URL")) {
					
						try {
							Stemmer radicalizador = Stemmer.StemmerFactory(StemmerType.PORTER);
							
							String radicalStr = radicalizador.getWordStem(str);
							
							if (str.length() == radicalStr.length()) {
								return true;
							}
							
						} catch (PTStemmerException e) {
							Logger.error(e.getMessage());
						}
					}
				}
			}		
		}
		return false;
	}

	public static String getRoot(String token) {
		try {
			Stemmer rooter = Stemmer.StemmerFactory(StemmerType.PORTER);
			return rooter.getWordStem(token);
		} catch (PTStemmerException e) {
			Logger.error(e.getMessage()); 
		}
		return null;
	}
	
}
