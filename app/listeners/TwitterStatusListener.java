package listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Event;
import models.Tweet;
import play.Logger;
import play.cache.Cache;
import system.Constants;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import utils.Utils;

import com.google.code.morphia.Key;

import dao.TweetDAO;
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
		
		try {

			if (!status.isRetweet() && status.getUser().getLang().equals("pt")) {
				
				// Quebrar o tweet em tokens e verificar cada palavra com o 'Cache' para pegar o Evento associado
				String[] tokens = status.getText().toLowerCase().split(" ");
				
				HashMap<Event, HashMap<String, TypeEnum>> keywordMap = (HashMap<Event, HashMap<String, TypeEnum>>) Cache.get("keywordMap");
				
					
				Set<Event> eventsOnCache = keywordMap.keySet();
				
				for (Event event : eventsOnCache) {
	
					// Create new List with event keywords
					HashMap<String, TypeEnum> map = keywordMap.get(event);
					List<String> keywords = new ArrayList<String>();
					Set<String> ks = map.keySet();
					for (String k : ks) {
						if (map.get(k).equals(TypeEnum.TEXT)) {
							keywords.add(k);
						} 
					}
					
					// And compare if the array of tokens generatate from tweets contains any of the keywords
					
					if (Utils.verifyArrayContains(tokens, keywords) == true) {
						
						// TODO: TENTAR MELHORAR O DESEMPENHO AQUI
						Tweet tweet = new Tweet();
						tweet.setEvent(new Key<Event>(Event.class, event.getId()));
						tweet.setText(status.getText());
						tweet.setTweetId(status.getId());
						tweet.setTwitterUserId(status.getUser().getId());
						tweet.setRetweeted(status.isRetweet());
						tweet.setRetweet_count(status.getRetweetCount());
						tweet.setProfile_image_url(status.getUser().getOriginalProfileImageURL());
						tweet.setCreatedAt(new Date(System.currentTimeMillis()));
						if (status.getGeoLocation() != null) {
							tweet.setGeoLocation(new models.GeoLocation(status.getGeoLocation().getLongitude(), status.getGeoLocation().getLatitude()));
						}
						
						List<Tweet> tweets = (List<Tweet>) Cache.get("tweets");
						int size = tweets.size();
						// Verify it cache is full
						// If is full, save to DB and clear cache
						// else, just add to cache
						tweets.add(tweet);
						if (size < Constants.CACHE_MAX_TWEETS) {
							
							Cache.set("tweets", tweets);
							
						} else {
							TweetDAO dao = new TweetDAO();
							
							dao.saveCollection(tweets);
							
							Cache.set("tweets", new ArrayList<Tweet>());
								
							Logger.info("Saving "+size+" tweets");
							Logger.info("Reseting tweets cache");
							
							Cache.set("has_new_tweets_on_db", new Boolean(true));
						}
						
						
					} 
					
				}
				
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
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
