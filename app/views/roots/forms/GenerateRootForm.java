package views.roots.forms;

import java.util.List;

import org.bson.types.ObjectId;

import ptstemmer.Stemmer.StemmerType;

public class GenerateRootForm {

	private List<ObjectId> selectedEvents;
	
	private int cutValue;
	
	private StemmerType algoritm;
	
	private boolean removeAcentuation;
	
	public GenerateRootForm() {}

	public List<ObjectId> getSelectedEvents() {
		return selectedEvents;
	}

	public void setSelectedEvents(List<ObjectId> selectedEvents) {
		this.selectedEvents = selectedEvents;
	}

	public int getCutValue() {
		return cutValue;
	}

	public void setCutValue(int cutValue) {
		this.cutValue = cutValue;
	}

	public StemmerType getAlgoritm() {
		return algoritm;
	}

	public void setAlgoritm(StemmerType algoritm) {
		this.algoritm = algoritm;
	}

	public boolean isRemoveAcentuation() {
		return removeAcentuation;
	}

	public void setRemoveAcentuation(boolean removeAcentuation) {
		this.removeAcentuation = removeAcentuation;
	}
	
	
}
