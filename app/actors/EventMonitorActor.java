package actors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Event;
import play.cache.Cache;
import system.Singletons;
import akka.actor.UntypedActor;

import com.google.code.morphia.Datastore;

import dao.EventDAO;
import enums.Situation;
import enums.TypeEnum;

/**
 * Actor that will monitor the start/finish times for keywords
 * and set them as active or not
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class EventMonitorActor extends UntypedActor {
	
	Datastore ds = Singletons.datastore;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object object) {
		
		
		EventDAO dao = new EventDAO();
		
		//verificar se as keywords que estao no Cache sao iguais as que estao ativas no banco
		
		List<Event> activeEvents = dao.listBySituation(Situation.STARTED);
		
		HashMap<String, HashMap<String, TypeEnum>> keywordMap = (HashMap<String, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
		Set<String> eventKeys = keywordMap.keySet();
		
		System.out.println("Active events on DB: "+activeEvents.size());
		System.out.println("Active events on CACHE: "+eventKeys.size());
		if (activeEvents.size() != eventKeys.size()) {
			System.out.println("Syncronizing cached keywords");
			syncCacheWith(activeEvents);
			System.out.println("Restarting twitter streamming");
			restartStream();
		}
		

		
		List<Event> events = dao.listBySituation(Situation.STARTED, Situation.NEVER_STARTED);
		
		Date now = new Date(System.currentTimeMillis());
		
		for (Event event : events) {
			
			
			if (event.getSituation().equals(Situation.NEVER_STARTED)) {
				// Event Never Started
				if (now.after(event.getStartDate()) && now.before(event.getFinishDate())) {
					System.out.println("Iniciando "+event.getName());
					dao.setSituation(event.getId(), Situation.STARTED);
					
				}
			} else {
				// Event Started
				if(now.after(event.getFinishDate())) {
					System.out.println("Finalizando "+event.getName());
					dao.setSituation(event.getId(), Situation.FINISHED);
					
				}
			}
		}
		
		
	}

	/**
	 * Method that syncronize the cache with keywords of active events on DB
	 * 
	 * @param activeEvents
	 */
	private void syncCacheWith(List<Event> activeEvents) {
		
		HashMap<String, HashMap<String, TypeEnum>> keywordMap = new HashMap<String, HashMap<String, TypeEnum>>();
		
		for (Event event : activeEvents) {
			
			keywordMap.put(event.getName(), event.getKeywords());
			
		}
		Cache.set("keywordMap", keywordMap);
	}

	private void restartStream() {

		// Get Active Events on CACHE
		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TypeEnum>> keywordMap = (HashMap<String, HashMap<String, TypeEnum>>) Cache.get("keywordMap");

		// Separete Keywords(#hashtags / normal_keywords) from @users
		Set<String> keys = keywordMap.keySet();
		List<String> keywords = new ArrayList<String>();
		List<String> users = new ArrayList<String>();
		for (String key : keys) {

			HashMap<String, TypeEnum> map = keywordMap.get(key);
			Set<String> ks = map.keySet();
			for (String k : ks) {
				if (map.get(k).equals(TypeEnum.TEXT)) {
					keywords.add(k);
				} else if(map.get(k).equals(TypeEnum.BOTH)) {
					keywords.add(k);
					users.add(k);
				} else {
					users.add(k);
				}
			}
		}
		
		
		restartTwitterStream(keywords);
		
		restartUserStream(users);
		
		
	}

	private void restartTwitterStream(List<String> keywords) {
		// TODO Auto-generated method stub
		
	}
	
	private void restartUserStream(List<String> users) {
		// TODO Auto-generated method stub
		
	}


}
