package com.tweetapp.TweetApplication.exception;

public class UserExistsException extends Exception {

	public UserExistsException(String message) {
		super(message);
	}
}
