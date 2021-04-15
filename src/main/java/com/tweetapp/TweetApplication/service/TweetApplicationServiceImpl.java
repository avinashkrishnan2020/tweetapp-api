package com.tweetapp.TweetApplication.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tweetapp.TweetApplication.dao.TweetsRepository;
import com.tweetapp.TweetApplication.dao.UsersRepository;
import com.tweetapp.TweetApplication.exception.InvalidUsernameException;
import com.tweetapp.TweetApplication.exception.PasswordMismatchException;
import com.tweetapp.TweetApplication.exception.TweetDoesNotExistException;
import com.tweetapp.TweetApplication.exception.UserExistsException;
import com.tweetapp.TweetApplication.exception.UserNotRegisteredException;
import com.tweetapp.TweetApplication.model.Tweet;
import com.tweetapp.TweetApplication.model.UserDetails;
import com.tweetapp.TweetApplication.producer.TweetAppProducer;

import io.micrometer.core.instrument.util.StringUtils;

@Component
public class TweetApplicationServiceImpl {

	@Autowired
	TweetsRepository tweetsRepo;

	@Autowired
	UsersRepository usersRepo;
	
	@Autowired
	TweetAppProducer logProducer;

	public List<Tweet> getAllTweets() {
		return tweetsRepo.findAll();
	}

	// Method to register new User
	public UserDetails registerNewUser(UserDetails newUser) throws UserExistsException {
		
		//Get list of registered users
		List<UserDetails> registeredUsers = usersRepo.findAll();

		// Check if the given userId and email already exists
		if (registeredUsers != null && !registeredUsers.isEmpty()) {
			Optional<UserDetails> registeredUserOptional = registeredUsers.stream()
					.filter(user -> user.getLoginId().equalsIgnoreCase(newUser.getLoginId())
							|| user.getEmail().equalsIgnoreCase(newUser.getEmail()))
					.findAny();

			if (!registeredUserOptional.isPresent()) {
				//If email and userId are unique, register new user
				logProducer.logUserRegistrationEvents(newUser);
				usersRepo.insert(newUser);
				return newUser;
			} else {
				throw new UserExistsException("User with given userId or email already exists.");
			}
		} else {
			logProducer.logUserRegistrationEvents(newUser);
			usersRepo.insert(newUser);
			return newUser;
		}

	}

	//Method to validate user login details
	public UserDetails validateUser(UserDetails userLoginDetails) throws PasswordMismatchException, UserNotRegisteredException {

		String loginId = userLoginDetails.getLoginId();
		String password = userLoginDetails.getPassword();
		
		Optional<UserDetails> user = usersRepo.findById(loginId);
		
		// If user object found, it is a registered user. Else, user is not registered.
		if (user.isPresent() && user.get() != null) {
			if (user.get().getPassword().contentEquals(password)) {
				// return user details if password matches else throw exception
				return user.get();
			} else {
				throw new PasswordMismatchException("Mismatch in password provided");
			}
		} else {
			// Throw exception if user is not registered
			throw new UserNotRegisteredException("User with given loginId cannot be found");
		}
	}

	// Method to return a list of all users
	public List<UserDetails> getAllUsers() {
		return usersRepo.findAll();
	}

	//Method to return all of a user's tweets
	public List<Tweet> getUserTweets(String username) throws InvalidUsernameException {
		// use username as login id
		if(!StringUtils.isBlank(username)) {
			return tweetsRepo.findAll().stream().filter(tweet -> tweet.getUserId().contentEquals(username))
					.collect(Collectors.toList());

		} else {
			throw new InvalidUsernameException("Username/loginId provided is invalid");
		}
		
	}

	//Method to post a new tweet
	public void postNewTweet(String username, Tweet newTweet) {
		
		newTweet.setTweetId(UUID.randomUUID().toString());
		logProducer.logNewPostEvents(newTweet);
		tweetsRepo.insert(newTweet);
		
	}

	//Method to update an existing tweet
	public Tweet updateTweet(String userId, String tweetId, Tweet updatedTweet) throws TweetDoesNotExistException {
		
		updatedTweet.setTweetId(tweetId);
		Optional<Tweet> originalTweetOptional = tweetsRepo.findById(tweetId);
		if(originalTweetOptional.isPresent()) {
			logProducer.logEditPostEvents(updatedTweet, originalTweetOptional.get());
			tweetsRepo.save(updatedTweet);
			return updatedTweet;
		} else {
			throw new TweetDoesNotExistException("This tweet does not exist anymore."); 
		}
		
		
	}

	//Method to delete a tweet
	public boolean deleteTweet(String tweetId) throws TweetDoesNotExistException {
		if(tweetsRepo.existsById(tweetId) && !StringUtils.isBlank(tweetId)) {
			logProducer.logDeletePostEvents(tweetsRepo.findById(tweetId).get());
			tweetsRepo.deleteById(tweetId);
			return true;
		} else {

			throw new TweetDoesNotExistException("This tweet does not exist anymore.");
		}
	}

	
	//Method to comment on a tweet
	public Tweet replyTweet(String userId, String tweetId, Tweet tweetReply) throws TweetDoesNotExistException {
		Optional<Tweet> tweetOptional = tweetsRepo.findById(tweetId);
		if(tweetOptional.isPresent()) {
			Tweet tweet = tweetOptional.get();
			tweetReply.setTweetId(UUID.randomUUID().toString());
			tweet.getComments().add(tweetReply);
			tweetsRepo.save(tweet);
			return tweetReply;
		} else {	
			throw new TweetDoesNotExistException("This tweet does not exist anymore.");
		}
	}
	
	//Method to change a user's password
	public UserDetails changePassword(String loginId, String newPassword) throws Exception {
		Optional<UserDetails> userDetailsOptional = usersRepo.findById(loginId);
		
		if(userDetailsOptional.isPresent()) {
			UserDetails user = userDetailsOptional.get(); 
			user.setPassword(newPassword);
			logProducer.logPasswordChangeEvents(user);
			return usersRepo.save(user);
			
		} else {
			throw new Exception("Unable to change password");
		}
	}

}
