package utils;

import java.util.List;

public class Utils {

	
	/**
	 * Verify if the keywords list contains any of the tokens
	 * @param tokens
	 * @param keywords
	 * @return
	 */
	public static boolean verifyArrayContains(String[] tokens, List<String> keywords) {
		
		
		for (int i = 0; i < tokens.length; i++) {
			if(contains( tokens[i], keywords )) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean contains(String str, List<String> list) {
		
		for (String string : list) {
			if (str.contains(string)) {
				return true;
			}
		}
		
		return false;
	}

	
}
