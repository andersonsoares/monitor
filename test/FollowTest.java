import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class FollowTest {

	private static String CONSUMER_KEY2 = "1fUyBflftkkW2zsxspwDQ";
	private static final String CONSUMER_SECRET2 = "STyruGMShCt3nAwBaITT1N3iute7bXIo3TvUTLsyxCw";
	private static final String ACCESS_TOKEN2 = "1366473476-cbXy2moLqFComM1o2pGt1oneWMfLLfjMmgSXxPL";
	private static final String ACCESS_TOKEN_SECRET2 = "DzpRIUPT1Rp9ovMkwaRGBGBPQOnVX53VgNjULcNy8";
	
	public static void main(String[] args) {
	
		Twitter t = TwitterFactory.getSingleton();
		t.setOAuthConsumer(CONSUMER_KEY2, CONSUMER_SECRET2);
		t.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN2, ACCESS_TOKEN_SECRET2));
		try {
			User u = t.verifyCredentials();
			System.out.println(u.getName());
			
			
//			t.createFriendship("g1");
//			
//			ResponseList<User> res = t.lookupUsers(new String[]{
//					"rafinhabastos","andersonsoares","rsmourapi", "g1",
//					"ufpi", "r7"
//					
//					});
//			for (User user : res) {
//				
//				System.out.println(user.getScreenName()+" - "+user.getName()+" - "+user.getName().toLowerCase().equals("g1")+" - "+user.getId());
//				
//			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
