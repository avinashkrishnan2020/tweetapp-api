package com.tweetapp.TweetApplication.exception;

public class TweetDoesNotExistException extends Exception{

	public TweetDoesNotExistException(String message) {
		super(message);
	}
}
