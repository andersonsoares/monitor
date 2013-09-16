package utils;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Json;
import system.Singletons;
import system.mongodb.CurrentOp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

/**
 * Class to get usefull informations from MongoDB Status
 * @author aers
 *
 */
public class MongoDBUtils {

	/**
	 * Method to verify mongodb 'currentOp()'
	 * This mongodb method return all current operations running on the DB
	 * This method below verify this method and search for attribute
	 * 'secs_running' to verify for how long a operation is running.
	 * If there is operation running for more the 10secs, this method
	 * return true;
	 * 
	 * Consult result example:
	 * ns - monitor.tweets
	 * numYields - 1
	 * desc - conn22
	 * op - query
	 * client - 127.0.0.1:63773
	 * threadId - 0x30cd43000
	 * query - {$query={eventAnalysis.52331568036476c5bd6fb238=POSITIVE}, $orderby={createdAt=-1}}
	 * connectionId - 22
	 * opid - 567040
	 * lockStats - {timeLockedMicros={r=369381, w=0}, timeAcquiringMicros={r=184743, w=0}}
	 * waitingForLock - false
	 * locks - {^=r, ^monitor=R}
	 * active - true
	 * secs_running - 0
	 * 
	 * @return If there is operation running for more the 10secs, this method return true;
	 */
	public static boolean verifyIfThereIsLongCurrentOpRunning() {
		
		Singletons.mongo.getDB("admin").authenticate("aers", "08n10398".toCharArray());
		
		DBCursor cur = Singletons.mongo.getDB("admin").getCollection("$cmd.sys.inprog").find();
		BasicDBObject dbOp;
	    while (cur.hasNext()) {
	    	dbOp = (BasicDBObject) cur.next();
	    	JsonNode currentOpString = Json.parse(dbOp.toString());
	    	CurrentOp currentOp = Json.fromJson(currentOpString, CurrentOp.class);
	    	for (Map<String, Object> map : currentOp.getInprog()) {
	    		if (map.containsKey("secs_running") && map.containsKey("client")) {
	    			if ((int)map.get("secs_running") > 10) {
	    				Logger.warn("Found currentOp running for more than 10secs... badRequest");
	    				return true;
	    			}
	    		}
			}
	    }
		
		return false;
	}
	
	
}
