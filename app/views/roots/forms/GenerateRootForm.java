package views.roots.forms;

import java.util.List;

import org.bson.types.ObjectId;

public class GenerateRootForm {

	private List<ObjectId> selectedEvents;
	
	private int cutValue;
	
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
	
	
}
