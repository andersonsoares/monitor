package system;

import org.bson.types.ObjectId;

/**
 * class that represents a status progress of roots generations
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class StatusProgress {

	private final ObjectId eventId;
	private final float progress;
	
	
	public StatusProgress(ObjectId eventId, float progress) {
		super();
		this.eventId = eventId;
		this.progress = progress;
	}

	public ObjectId getEventId() {
		return eventId;
	}


	public float getProgress() {
		return progress;
	}
	
	
}
