package actors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Event;
import play.cache.Cache;
import system.Singletons;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
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
		
		HashMap<Event, HashMap<String, TypeEnum>> keywordMap = (HashMap<Event, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
		Set<Event> eventsOnCache = keywordMap.keySet();
		
		if (activeEvents.size() != eventsOnCache.size()) {
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
		
		HashMap<Event, HashMap<String, TypeEnum>> keywordMap = new HashMap<Event, HashMap<String, TypeEnum>>();
		
		for (Event event : activeEvents) {
			
			keywordMap.put(event, event.getKeywords());
			
		}
		Cache.set("keywordMap", keywordMap);
	}

	private void restartStream() {

		// Get Active Events on CACHE
		@SuppressWarnings("unchecked")
		HashMap<Event, HashMap<String, TypeEnum>> keywordMap = (HashMap<Event, HashMap<String, TypeEnum>>) Cache.get("keywordMap");

		// Separete Keywords(#hashtags / normal_keywords) from @users
		Set<Event> events = keywordMap.keySet();
		List<String> keywords = new ArrayList<String>();
		List<String> users = new ArrayList<String>();
		for (Event event : events) {

			HashMap<String, TypeEnum> map = keywordMap.get(event);
			Set<String> ks = map.keySet();
			for (String k : ks) {
				if (map.get(k).equals(TypeEnum.TEXT)) {
					keywords.add(k);
				} else if(map.get(k).equals(TypeEnum.BOTH)) {
					keywords.add('@'+k);
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
		
		Singletons.twitterStream.shutdown();
		Singletons.twitterStream.cleanUp();
		
		String[] keywordsArray = new String[keywords.size()];
		keywordsArray = keywords.toArray(keywordsArray);
		
		FilterQuery filter = new FilterQuery();
		filter.track(keywordsArray);
		
		Singletons.twitterStream.filter(filter);
		
	}
	
	private void restartUserStream(List<String> users) {

		try {
			// primeiro crio amizade com todos os users passados como parametro
			// caso a amizade ainda nao exista...
			String[] usersArray = new String[users.size()];
			usersArray = users.toArray(usersArray);
			
			ResponseList<User> usersToFollow = Singletons.twitter.lookupUsers(usersArray);
		
			List<Long> usersToFollowIDs = new ArrayList<Long>(); 
			long[] myFriendsIDs = Singletons.twitter.getFriendsIDs(-1).getIDs();// 0 = cursor, this application will not have 5000+ friends
			
			for (User user : usersToFollow) {
			
				if (!alreadyFollowing(user.getId(),myFriendsIDs)) {
					System.out.println("Creating relationship with: "+user.getScreenName());
					Singletons.twitter.createFriendship(user.getScreenName());
				}
				usersToFollowIDs.add(user.getId());
			}
		
			
			
			for (long l : myFriendsIDs) {
				if (!usersToFollowIDs.contains(l)) {
					System.out.println("Removing friend ID: "+l);
					Singletons.twitter.destroyFriendship(l);
				}
			}

		
		
		
		
			// depois eu vejo minha lista de amigos e comparo com a de users
			
			Singletons.userStream.shutdown();
			Singletons.userStream.cleanUp();
			
			Singletons.userStream.user();
		
		} catch (TwitterException e) {
			e.printStackTrace();
		} 
		
	}

	private boolean alreadyFollowing(long id, long[] myFriendsIDs) {
		
		for (long l : myFriendsIDs) {
			if (l == id) {
				return true;
			}
		}
		
		return false;
	}


}
