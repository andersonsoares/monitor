package system;

import java.util.HashMap;

/**
 * class that represents a return to view
 * like success / error messages
 * redirect urls..
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class ReturnToView {
	
	/*
	 * code : 200 -> success (default)
	 * code : 400 -> error
	 */
	private int code = 200;
	
	private String message;
	
	private String redirectUrl;
	
	private HashMap<String, Object> map = new HashMap<String, Object>();
	
	public ReturnToView(){}
	
	public ReturnToView(int code, String message, String redirectUrl,
			HashMap<String, Object> map) {
		super();
		this.code = code;
		this.message = message;
		this.redirectUrl = redirectUrl;
		this.map = map;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}
	
	

}
