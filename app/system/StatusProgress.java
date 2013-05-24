package system;


/**
 * class that represents a status progress of roots generations
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class StatusProgress {

	private final String eventId;
	private final float progress;
	
	
	public StatusProgress(String eventId, float progress) {
		super();
		this.eventId = eventId;
		this.progress = progress;
	}

	public String getEventId() {
		return eventId;
	}


	public float getProgress() {
		return progress;
	}
	
	
}
