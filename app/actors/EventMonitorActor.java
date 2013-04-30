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
	
	@Override
	public void onReceive(Object object) {
		
		EventDAO dao = new EventDAO();
		List<Event> events = dao.listBySituation(Situation.STARTED, Situation.NEVER_STARTED);
		Date now = new Date(System.currentTimeMillis());
		
		for (Event event : events) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, TypeEnum>> keywordMap = (HashMap<String, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
			
			if (event.getSituation().equals(Situation.NEVER_STARTED)) {
				// Event Never Started
				if (now.after(event.getStartDate())) {
					dao.setSituation(event.getId(), Situation.STARTED);
					
					keywordMap.put(event.getNameLowerCase(), event.getKeywords());
					Cache.set("keywordMap", keywordMap);
					
					restartStream();
					
				}
			} else {
				// Event Started
				if(now.after(event.getFinishDate())) {
					dao.setSituation(event.getId(), Situation.FINISHED);
					
					keywordMap.remove(event.getNameLowerCase());
					Cache.set("keywordMap", keywordMap);
					
					restartStream();
					
				}
			}
		}
		
		
	}

	private void restartStream() {
		System.out.println("Restarting stream");
		
		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TypeEnum>> keywordMap = (HashMap<String, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
		
		Set<String> keys = keywordMap.keySet();
		List<String> keywords = new ArrayList<String>();
		List<String> users = new ArrayList<String>();
		for (String key : keys) {
//			keywords.addAll(keywordMap.get(key).keySet());
			
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
		
		System.out.println("Active keywords: "+keywords.size());
		for (String string : keywords) {
			System.out.println(string);
		}
		System.out.println("Active users: "+users.size());
		for (String string : users) {
			System.out.println(string);
		}
		
		
		
	}

}
