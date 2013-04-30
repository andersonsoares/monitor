import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class FollowTest {

	private static String CONSUMER_KEY2 = "aLLUylFk1W1sD9VV4LMDWA";
	private static final String CONSUMER_SECRET2 = "5pMvboOc1G461u4450mnQk0aO0oc2665QdYpIq8GOg";
	private static final String ACCESS_TOKEN2 = "1366473476-GKe7jKMmvkkK4mmFgV7j3MdhDp8LvN9XWvXWHeD";
	private static final String ACCESS_TOKEN_SECRET2 = "6u9zZ5n47v3pPhuAm4B8F8HZEwnI0iuOtBqSVSEsLY";
	
	public static void main(String[] args) {
	
		Twitter t = TwitterFactory.getSingleton();
		t.setOAuthConsumer(CONSUMER_KEY2, CONSUMER_SECRET2);
		t.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN2, ACCESS_TOKEN_SECRET2));
		try {
			User u = t.verifyCredentials();
			System.out.println(u.getName());
			
			
			ResponseList<User> res = t.lookupUsers(new String[]{"rafinhabastos"});
			for (User user : res) {
				System.out.println(user.getName()+" - "+user.getName().toLowerCase().equals("g1")+" - "+user.getId());
				
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
