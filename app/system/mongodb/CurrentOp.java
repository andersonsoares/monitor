package system.mongodb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that represent a CurrentOp result
 * when running 'db.currentOp()'
 * This class represent the serialization of CurrentOp from json
 * @author aers
 *
 */
public class CurrentOp {

	List<HashMap<String, Object>> inprog = new ArrayList<HashMap<String,Object>>();

	public CurrentOp() {
	}
	
	public List<HashMap<String, Object>> getInprog() {
		return inprog;
	}

	public void setInprog(List<HashMap<String, Object>> inprog) {
		this.inprog = inprog;
	}
	
	
	
}
