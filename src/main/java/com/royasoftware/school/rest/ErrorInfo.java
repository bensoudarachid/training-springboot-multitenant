package com.royasoftware.school.rest;

import java.util.Map;

public class ErrorInfo {
	private String url;
	private String error;
	private String errorDescription;
	private Map<String,String> validation;

	public ErrorInfo() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String message) {
		this.errorDescription = message;
	}
	
	public Map<String,String> getValidation() {
		return validation;
	}

	public void setValidation(Map validation) {
		this.validation = validation;
	}

}