package models.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.format.Formats.DateTime;

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
	
	@DateTime(pattern="dd/MM/yyyy hh:mm:ss")
	private Date startDate;
	@DateTime(pattern="dd/MM/yyyy hh:mm:ss")
	private Date finishDate;

	/*
	 * acceptable correct rate
	 */
	private float correctRate;
	
	private List<String> considerWhat = new ArrayList<String>();
	
	/**
	 * Constructor
	 */
	public GetTweetsForm() {}

	/**
	 * Getters and setters
	 * @return
	 */
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
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getFinishDate() {
		return finishDate;
	}
	
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
}
