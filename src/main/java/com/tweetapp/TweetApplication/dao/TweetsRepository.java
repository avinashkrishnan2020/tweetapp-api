package com.tweetapp.TweetApplication.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.tweetapp.TweetApplication.model.Tweet;

@Component
public interface TweetsRepository extends MongoRepository<Tweet, String> {

}
