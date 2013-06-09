package system;

public class ValidationError {

	private String fieldName;
	
	private String errorMsg;
	
	public ValidationError(String fieldName, String errorMsg) {
		this.fieldName = fieldName;
		this.errorMsg = errorMsg;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
