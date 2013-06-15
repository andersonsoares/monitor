package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Abbreviation;
import play.Logger;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;

import com.google.common.base.CharMatcher;


/**
 * Class that contains some usefull PLN functions
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class PLNUtils {

	
	public static String replaceURLs(String str) {
		return str.replaceAll("((www\\.[\\s]+)|(https?://[^\\s]+))", "URL");
	}
	
	public static String replaceAndRemoveURLs(String str) {
		return str.replaceAll("((www\\.[\\s]+)|(https?://[^\\s]+))", "");
	}
	
	public static String replaceUSERs(String str) {
		return str.replaceAll("@[^\\s]+", "USER");
	}
	
	public static String replaceAndRemoveUSERs(String str) {
		return str.replaceAll("@[^\\s]+", "");
	}
	
	public static String replaceHASHTAG(String str) {
		return str.replaceAll("#[^\\s]+", "HASHTAG");
	}
	
	public static String replaceAndRemoveHASHTAG(String str) {
		return str.replaceAll("#[^\\s]+", "");
	}
	
	public static String removeAccents(String str) {
		return Normalizer.normalize(str, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	public static String removeNonAsciiChars(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;
	}
	
	public static String removePunctuation(String str) {
		return str.replaceAll("\\p{Punct}+", " ");
	}
	
	public static boolean containsRepeatedSequencesUpper(String str) {
		Pattern pattern = Pattern.compile("([A-Z])\\1{2}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		} else {
			pattern = Pattern.compile("([A-Z][A-Z])\\1{1}");
			matcher = pattern.matcher(str);
			if (matcher.find()) {
				return true;
			} else {
				pattern = Pattern.compile("([A-Z][A-Z][A-Z])\\1{1}");
				matcher = pattern.matcher(str);
				if (matcher.find()) {
					return true;
				} else {
					pattern = Pattern.compile("([A-Z][A-Z][A-Z][A-Z])\\1{1}");
					matcher = pattern.matcher(str);
					if (matcher.find()) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}
	
	public static String tryFixRepeatedChars(String str) {
		return str.replaceAll("(([a-z|A-Z])\\2{2,})?", "$2");
	}
	
	public static String tryFixTwoRepeatedChars(String str) {
		return str.replaceAll("(([a-z|A-Z][a-z|A-Z])\\2{2,})?", "$2");
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
	
	public static String removeHASHTAG(String str) {
		return str.replaceAll("HASHTAG", " ");
	}
	
	public static String removeDigits(String str) {
		return str.replaceAll("[0-9]", " ");
	}
	
	
//	public static List<String> getSiglas(String str, StemmerType algoritm) {
//		List<String> siglas = new ArrayList<String>();
//	
//		
//		str = removeRT(str);
//		str = replaceURLs(str);
//		str = replaceUSERs(str);
//		str = removeNonAsciiChars(str);
//		str = removePunctuation(str);
//		str = removeWhiteSpacesNotNecessary(str);
//		str = tryFixRepeatedChars(str);
//		
//		String[] tokens = str.split(" ");
//		for (String string : tokens) {
//			if (isSigla(string, algoritm)) {
//				siglas.add(string);
//			}
//		}
//		
//		return siglas;
//	}
	
	public static String normalizedTweet(String str) {
		
		str = str.toLowerCase();
		str = removeRT(str);
		str = tryFixRepeatedChars(str);
		str = tryFixTwoRepeatedChars(str);
		str = replaceURLs(str);
		str = removeURLs(str);
		str = replaceUSERs(str);
		str = removeUSERs(str);
		str = removeDigits(str);
		
//		str = removeAccents(str);
		str = removeNonAsciiChars(str);
		
		str = removePunctuation(str);
		str = removeWhiteSpacesNotNecessary(str);
		
		return str;
	}
	
	public static String clearString(String str) {
		str = str.toLowerCase();
		
		str = removeNonAsciiChars(str);
		str = removeDigits(str);
		str = removePunctuation(str);
		str = removeWhiteSpacesNotNecessary(str);
		str = str.replace(" ", "_");
		
		return str;
	}
	
	/**
	 * Metodo que verifica todas as palavras MAIUSCULAS
	 * do tweet, e verifica se elas existem no dicionario
	 * se nao existir, ela é uma possível sigla
	 * @param tweet
	 * @return
	 */
	public static HashSet<Abbreviation> getAbreviacoes(String texto, HashSet<String> dicionario) {
		HashSet<Abbreviation> abreviacoes = new HashSet<Abbreviation>();
		
		texto = PLNUtils.replaceAndRemoveHASHTAG(texto);
		texto = PLNUtils.replaceAndRemoveURLs(texto);
		texto = PLNUtils.replaceAndRemoveUSERs(texto);
		texto = PLNUtils.removeDigits(texto);
		texto = PLNUtils.removePunctuation(texto);
		texto = PLNUtils.removeWhiteSpacesNotNecessary(texto);
		
		String[] tokens = texto.split(" ");
		for (String string : tokens) {
			if (isSigla(string)) {
				if (!dicionario.contains(string.toLowerCase())) {
					abreviacoes.add(new Abbreviation(string));
				}
			}
		}
		
		
		return abreviacoes;
	}
	
	public static boolean isSigla(String str) {
		if (str.length() > 1 && str.length() < 6 && CharMatcher.JAVA_UPPER_CASE.matchesAllOf(str) && !containsRepeatedSequencesUpper(str.toUpperCase()) && CharMatcher.ASCII.matchesAllOf(str)) {
			return true;
		}
		return false;
	}
	

	/**
	 * Function that checks if a word could be a 'SIGLA'
	 * Verifies if the word if all UpperCase and then runs
	 * Stemmer to get its root.
	 * @param str
	 * @return
	 */
	public static boolean isSigla(String str, StemmerType algoritm) {
		
		if (str.length() >= 2) { 
			if (!str.substring(0,1).matches("[0-9]")) {
				
			
				// Verifies if String is all toUpperCase()
				if (str.equals(str.toUpperCase())) {
					
					if (!str.equals("USER") && !str.equals("URL")) {
					
						try {
							Stemmer radicalizador = Stemmer.StemmerFactory(algoritm);
							
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

	public static String getRoot(String token, StemmerType algoritm) {
		try {
			Stemmer rooter = Stemmer.StemmerFactory(algoritm);
			return rooter.getWordStem(token);
		} catch (PTStemmerException e) {
			Logger.error(e.getMessage()); 
		}
		return null;
	}
	
	public static HashSet<String> readDictionary(boolean considerAccents) {
		
		
		long inicio = System.currentTimeMillis();
		HashSet<String> words = new HashSet<String>();
		
		
		try {
		
			File file = new File("dicionario.txt");
			
			BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file), "UTF8"));
			
			String str;
			while((str = br.readLine()) != null) {
				if (considerAccents) {
					words.add(str.toLowerCase());
				} else {
					words.add(PLNUtils.removeAccents(str.toLowerCase()));
				}
				
				
			}
			
			br.close();
			Logger.info("Dictionary loaded in "+(System.currentTimeMillis() - inicio)+" ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return words;
	}
	
	public static float getCorrectRate(String tweet, HashSet<String> dicionario, List<Abbreviation> abbreviations, boolean considerHashtags, boolean considerURLs, boolean considerUSERs, boolean considerSIGLAs) {
		
		tweet = PLNUtils.removeRT(tweet);
		
		if (considerHashtags) {
			tweet = PLNUtils.replaceHASHTAG(tweet);			
		} else {
			tweet = PLNUtils.replaceAndRemoveHASHTAG(tweet);
		}
		if (considerURLs) {
			tweet = PLNUtils.replaceURLs(tweet);			
		} else {
			tweet = PLNUtils.replaceAndRemoveURLs(tweet);
		}
		if (considerUSERs) {
			tweet = PLNUtils.replaceUSERs(tweet);
		} else {
			tweet = PLNUtils.replaceAndRemoveUSERs(tweet);
		}
		
		tweet = PLNUtils.removeDigits(tweet);
		
		tweet = PLNUtils.removePunctuation(tweet);
		
		tweet = PLNUtils.removeWhiteSpacesNotNecessary(tweet);
		
		String[] tokens = tweet.split(" ");
		
		int correctWordsCount = 0;
		for (String string : tokens) {
			
			if (considerSIGLAs) {
				if (isSigla(string) && abbreviations.contains(new Abbreviation(string))) {
					correctWordsCount++;
					continue;
				}
			}
			if (string.equals("HASHTAG") || string.equals("USER") || string.equals("URL")) {
				correctWordsCount++;
			} else {
				if (dicionario.contains(string.toLowerCase())) {
					correctWordsCount++;
				}	
			}
				
		}
		
		float rate = (float)correctWordsCount / tokens.length;
		
		return rate;
	}
	
	
	
}
