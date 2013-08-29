package models.forms;

import java.util.ArrayList;
import java.util.List;

public class AnalyseCheckForm {
	
	private String tweet;
	
	private String dictionaryId;
	
	private List<String> considerWhat = new ArrayList<String>();
	
	public AnalyseCheckForm() {}

	public AnalyseCheckForm(String tweet, String dictionaryId, List<String> considerWhat) {
		super();
		this.setTweet(tweet);
		this.dictionaryId = dictionaryId;
		this.considerWhat = considerWhat;
	}

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public List<String> getConsiderWhat() {
		return considerWhat;
	}

	public void setConsiderWhat(List<String> considerWhat) {
		this.considerWhat = considerWhat;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	
	

}
