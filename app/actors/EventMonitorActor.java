package actors;

import java.util.Date;
import java.util.List;

import models.Event;
import system.Singletons;
import akka.actor.UntypedActor;

import com.google.code.morphia.Datastore;

import dao.EventDAO;
import enums.Situation;

/**
 * Actor that will monitor the start/finish times for keywords
 * and set them as active or not
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class EventMonitorActor extends UntypedActor {
	
	Datastore ds = Singletons.datastore;
	
	@Override
	public void onReceive(Object object) {
		
		EventDAO dao = new EventDAO();
		List<Event> events = dao.listBySituation(Situation.STARTED, Situation.NEVER_STARTED);
		Date now = new Date(System.currentTimeMillis());
		
		for (Event event : events) {
			
		}
		
		
	}
	
	private void verifyStartedEvents() {
		EventDAO dao = new EventDAO();
		List<Event> events = dao.listBySituation(Situation.STARTED);
		Date now = new Date(System.currentTimeMillis());
		System.out.println("Verifing started "+events.size());
		for (Event event : events) {
			if (now.after(event.getFinishDate())) {
				//TODO: EVENT EXPIRED!
				// set it as FINISHED
				System.out.println("Disabling event");
				dao.setSituation(event.getId(), Situation.FINISHED);
				// remove it from cache
				// restart stream?
			}
		}
	}

	private void verifyEvents() {
		EventDAO dao = new EventDAO();
		List<Event> events = dao.listBySituation(Situation.NEVER_STARTED);
		
		System.out.println("Verifing never started "+events.size());
		Date now = new Date(System.currentTimeMillis());
		for (Event event : events) {
			if (now.after(event.getStartDate())) {
				//TODO: NEW EVENT!
				// set it as STARTED
				System.out.println("Starting monitor new event");
				dao.setSituation(event.getId(), Situation.STARTED);
				// add it to cache
				
			}
		}
		
	}

}
