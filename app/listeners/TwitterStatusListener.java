package listeners;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

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

	@Override
	public void onStatus(Status status) {

		if (!status.isRetweet() && status.getUser().getLang().equals("pt")) {
			System.out.println(status.getUser().getLang()+" - "+status.getUser().getName()+" - "+status.getText());
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
