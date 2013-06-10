package models.forms;

public class AddWordForm {

	private String dictionaryId;
	private String wordString;
	
	public AddWordForm() {}

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public String getWordString() {
		return wordString;
	}

	public void setWordString(String wordString) {
		this.wordString = wordString;
	}
	
	
}
