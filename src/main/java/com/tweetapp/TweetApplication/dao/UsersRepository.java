package com.tweetapp.TweetApplication.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.tweetapp.TweetApplication.model.UserDetails;

@Component
public interface UsersRepository extends MongoRepository<UserDetails, String> {

}
