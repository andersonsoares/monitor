package listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Event;
import play.cache.Cache;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import enums.TypeEnum;

/**
 * Listener that will capture 'Twitter Stream' events like
 * new posted tweets, deleted tweets, exceptions
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class TwitterStatusListener implements StatusListener {

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStatus(Status status) {

//		if (!status.isRetweet() && status.getUser().getLang().equals("pt")) {
//			System.out.println(status.getUser().getLang()+" - "+status.getUser().getName()+" - "+status.getText());
			
			
			// Quebrar o tweet em tokens e verificar cada palavra com o 'Cache' para pegar o Evento associado
			String[] tokens = status.getText().split(" ");
			
			HashMap<Event, HashMap<String, TypeEnum>> keywordMap = (HashMap<Event, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
			
				
			Set<Event> eventsOnCache = keywordMap.keySet();
			
			for (Event event : eventsOnCache) {

				// Create new List with event keywords
				HashMap<String, TypeEnum> map = keywordMap.get(event);
				List<String> keywords = new ArrayList<String>();
				Set<String> ks = map.keySet();
				for (String k : ks) {
					if (map.get(k).equals(TypeEnum.TEXT) || map.get(k).equals(TypeEnum.BOTH)) {
						keywords.add(k);
					} 
				}
				
				// And compare if the array of tokens generatate from tweets contains any of the keywords
				
				if (verifyArrayContains(tokens, keywords) == true) {
					// add this tweet to the event(key)
					
					// TODO: TENTAR MELHORAR O DESEMPENHO AQUI
//					Tweet tweet = new Tweet();
//					tweet.setEvent(event);
					System.out.println(event.getName()+ " -> "+status.getText());
				}
				
			}
			
			
			
//		}
	}

	/**
	 * Verify if the keywords list contains any of the tokens
	 * @param tokens
	 * @param keywords
	 * @return
	 */
	private boolean verifyArrayContains(String[] tokens, List<String> keywords) {
		
		for (int i = 0; i < tokens.length; i++) {
			if(keywords.contains( tokens[i] )) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		// TODO Auto-generated method stub
		
	}

}
