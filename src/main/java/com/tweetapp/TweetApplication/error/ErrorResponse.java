package com.tweetapp.TweetApplication.error;

public class ErrorResponse {
	
	private String requestStatus;
	private String errorMessage;
	
	public ErrorResponse(String requestStatus, String errorMessage) {
		this.requestStatus = requestStatus;
		this.errorMessage = errorMessage;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
