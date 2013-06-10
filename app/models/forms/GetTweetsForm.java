package models.forms;

import java.util.ArrayList;
import java.util.List;

public class GetTweetsForm {

	/*
	 * all
	 * onlywithcorrectwords
	 */
	private boolean recoverAll;
	
	/*
	 * dictionary id
	 */
	private String dictionaryId;
	
	private String eventId;
	
	/*
	 * acceptable correct rate
	 */
	private float correctRate;
	
	private List<String> considerWhat = new ArrayList<String>();
	
	public GetTweetsForm() {}

	public boolean isRecoverAll() {
		return recoverAll;
	}

	public void setRecoverAll(boolean recoverAll) {
		this.recoverAll = recoverAll;
	}

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public float getCorrectRate() {
		return correctRate;
	}

	public void setCorrectRate(float correctRate) {
		this.correctRate = correctRate;
	}

	public List<String> getConsiderWhat() {
		return considerWhat;
	}

	public void setConsiderWhat(List<String> considerWhat) {
		this.considerWhat = considerWhat;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	
	
	
}
