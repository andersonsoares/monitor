package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Abbreviation;
import play.Logger;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;
import system.AnalyseCheck;
import system.Singletons;

import com.google.common.base.CharMatcher;

import enums.SentimentEnum;


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
		return str.replaceAll("(www\\.[\\s]+|https?://[^\\s]+)", "");
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
		return str.replaceAll("RT","");
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
	
	public static List<String> readFile(String fileName) {
		
		List<String> words = new ArrayList<String>();
		
		try {
		
			File file = new File(fileName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file), "UTF8"));
			
			String str;
			while((str = br.readLine()) != null) {
				words.add(str.toLowerCase());
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return words;
	}
	
	public static float getCorrectRate(String tweet, HashSet<String> dicionario, List<Abbreviation> abbreviations, boolean considerHashtags, boolean considerURLs, boolean considerUSERs, boolean considerSIGLAs) {
		
		tweet = normalizeText(tweet, considerHashtags, considerURLs,
				considerUSERs);
		
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
	


	/**
	 * Method that verifies if the tweet is 'correct' according with dictionary
	 * Analysing hashtags, urls, users and abbreviations(siglas)
	 * and correctRate*
	 * 
	 * After consider it correct or not, if its correct, then it verifies 
	 * if the tweet have sentiment negative/positive/neutral
	 * 
	 * @param text
	 * @param dictionaryWords
	 * @param abbreviations
	 * @param correctRate
	 * @param considerHashtag
	 * @param considerUser
	 * @param considerUrl
	 * @param considerSigla
	 * @return
	 */
	public static SentimentEnum analyseTweet(String text,
			HashSet<String> dictionaryWords, List<Abbreviation> abbreviations,
			float correctRate, boolean considerHashtag, boolean considerUser,
			boolean considerUrl, boolean considerSigla) {
		
		// normalize the text
		String normalizedTweet = normalizeText(text, considerHashtag, considerUrl, considerUser);
		
		float similarityRate = getSimilarityRate(normalizedTweet, dictionaryWords, abbreviations, considerSigla);
		// verify it similary rate is acceptable comparing with 'correctRate' passed as param
		if ((similarityRate*100) < correctRate) {
			return SentimentEnum.INCORRECT;
		} else {			
			return getSentimentAnalysis(normalizedTweet);
		}
	}
	
	
	/**
	 * Method that analyse the tweet tokens and verify if each
	 * token stemmed with Orengo Algorithm is in the
	 * positive or negative list of stemmed postivive and negative 
	 * words.
	 * Each time one appear, it is counted. In the end, if the 
	 * totalpositive == totalnegative OR totalnegative and totalpositive
	 * == 0, then it consider that it is a neutral tweet.
	 * @param tweet
	 * @return
	 */
	public static SentimentEnum getSentimentAnalysis(String tweet) {
		int totalPositiveStemmes = 0;
		int totalNegativeStemmes = 0;
		
		String[] tokens = tweet.split(" ");
		
		String tokenStemmed;
		for (String token : tokens) {
			
			tokenStemmed = Singletons.orengoStemmer.getWordStem(token);
			
			if (Singletons.positiveWords.contains(tokenStemmed)) {
				totalPositiveStemmes++;
			} else {
				if (Singletons.negativeWords.contains(tokenStemmed)) {
					totalNegativeStemmes++;
				}
			}
		}
		
		if (totalPositiveStemmes == totalNegativeStemmes || (totalPositiveStemmes == 0 && totalNegativeStemmes == 0)) {
			return SentimentEnum.NEUTRAL;
		} else {
			if (totalPositiveStemmes > totalNegativeStemmes) {
				return SentimentEnum.POSITIVE;
			} else {
				return SentimentEnum.NEGATIVE;
			}
		}
	}
	
	/**
	 * Method that verifies how correct is the 'normalized' text/tweet
	 * Its a simple arithmetic calc that counts the number of correct words
	 * based on dictionary passed as param, over the total words in the normalized text..
	 * considerSiglas will be used to count or not siglas as a word...
	 * 
	 * @param normalizedTweet
	 * @param dicionario
	 * @param abbreviations
	 * @param considerSIGLAs
	 * @return
	 */
	public static float getSimilarityRate(String normalizedTweet, HashSet<String> dicionario,
			List<Abbreviation> abbreviations, boolean considerSIGLAs) {
		
		String[] tokens = normalizedTweet.split(" ");
		
		int correctWordsCount = 0;
		for (String string : tokens) {
			
			if (considerSIGLAs) {
				if (isSigla(string) && abbreviations.contains(new Abbreviation(string))) {
					correctWordsCount++;
					if (!string.equals("USER") && !string.equals("URL")) {
						normalizedTweet = normalizedTweet.replace(string, "ABBREVIATION");
					}
					continue;
				}
			} else {
				if (isSigla(string) && abbreviations.contains(new Abbreviation(string))) {
					if (!string.equals("USER") && !string.equals("URL")) {
						normalizedTweet = normalizedTweet.replace(string, "");
					}
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
		
		tokens = normalizedTweet.split(" ");
		float rate = (float)correctWordsCount / tokens.length;
		
		return rate;
	}
	
	public static AnalyseCheck analyseCheck(String tweet, HashSet<String> dicionario,
			List<Abbreviation> abbreviations, boolean considerHashtag, boolean considerUser,
			boolean considerUrl, boolean considerSIGLAs) {
		
		tweet = normalizeText(tweet, considerHashtag, considerUrl, considerUser);
		String[] tokens = tweet.split(" ");
		
		int correctWordsCount = 0;
		StringBuilder sb = new StringBuilder("");
		for (String string : tokens) {
			
			if (considerSIGLAs) {
				if (isSigla(string) && abbreviations.contains(new Abbreviation(string))) {
					correctWordsCount++;
					if (!string.equals("USER") && !string.equals("URL")) {
						sb.append("ABBREVIATION").append(" ");
					}
					continue;
				} else {
					sb.append(string).append(" ");
				}
				
			} else {
				if (!isSigla(string) || !abbreviations.contains(new Abbreviation(string))) {
					sb.append(string).append(" ");
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
		
		AnalyseCheck analyseCheck = new AnalyseCheck(sb.toString(), rate, correctWordsCount, tokens.length);
		
		return analyseCheck;
	}
	
	/**
	 * Method that normalize a Tweet
	 * Remove 'RT' word
	 * Try fix some words like boooooom -> bom, oooootimo -> otimo
	 * If considerHashtag, replace all '#something' with 'HASHTAG', else replace with ''
	 * If considerUrls, replace all 'http:// | https:// | www.something...' with 'URL', else replace with ''
	 * If considerUser, replace all '@someone' with 'USER', else replace with ''
	 * Remove digits (0..9)*
	 * Remove punctuations -> .,!>?<{} .... not accentuation!
	 * Remove unnacessarie white spaces ;)
	 * 
	 * @param tweet
	 * @param considerHashtags
	 * @param considerURLs
	 * @param considerUSERs
	 * @return
	 */
	public static String normalizeText(String tweet, boolean considerHashtags,
			boolean considerURLs, boolean considerUSERs) {
		tweet = PLNUtils.removeRT(tweet);
		
		tweet = PLNUtils.tryFixRepeatedChars(tweet);
		
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
		if (tweet.startsWith(" "))
			tweet = tweet.substring(1);
		return tweet;
	}
	
}
