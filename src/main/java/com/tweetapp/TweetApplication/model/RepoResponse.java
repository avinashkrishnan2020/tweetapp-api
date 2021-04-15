package com.tweetapp.TweetApplication.model;

public class RepoResponse {
	
	private boolean acknowledged;
	private boolean insertedId;
	
	public boolean isAcknowledged() {
		return acknowledged;
	}
	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	public boolean isInsertedId() {
		return insertedId;
	}
	public void setInsertedId(boolean insertedId) {
		this.insertedId = insertedId;
	}
	
	
}
