package com.royasoftware.school.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends Exception{
	
	private Object object;
	private Map validationErrorMap;
	
	public String getMessage() {
		return "Invalid "+object.getClass().getName().toLowerCase();
	}
	public Map getValidationErrorMap() {
		return validationErrorMap;
	}

	public void setValidationErrorMap(Map validationErrorMap) {
		this.validationErrorMap = validationErrorMap;
	}

	public ValidationException(Object o, Map validationErrorMap) {
		super();
		object = o;
		this.validationErrorMap = validationErrorMap;
	}

	  
}
