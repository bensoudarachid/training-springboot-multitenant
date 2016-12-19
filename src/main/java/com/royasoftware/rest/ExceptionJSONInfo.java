package com.royasoftware.rest;

public class ExceptionJSONInfo {
	private String url;
	private String error;
	private String errorDescription;

	public ExceptionJSONInfo() {
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
}