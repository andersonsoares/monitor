package models.forms;

import java.util.ArrayList;
import java.util.List;

public class AnalyseForm {

	/*
	 * dictionary id
	 */
	private String dictionaryId;
	
	private String eventId;
	
	/*
	 * acceptable correct rate
	 */
	private float correctRate;
	
	private String email;
	
	private List<String> considerWhat = new ArrayList<String>();
	
	/**
	 * Constructor
	 */
	public AnalyseForm() {}

	/**
	 * Getters and setters
	 * @return
	 */


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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
