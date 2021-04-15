package com.tweetapp.TweetApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.TweetApplication.error.ErrorResponse;
import com.tweetapp.TweetApplication.exception.InvalidUsernameException;
import com.tweetapp.TweetApplication.exception.PasswordMismatchException;
import com.tweetapp.TweetApplication.exception.TweetDoesNotExistException;
import com.tweetapp.TweetApplication.exception.UserExistsException;
import com.tweetapp.TweetApplication.exception.UserNotRegisteredException;
import com.tweetapp.TweetApplication.model.Tweet;
import com.tweetapp.TweetApplication.model.UserDetails;
import com.tweetapp.TweetApplication.service.TweetApplicationServiceImpl;

@RestController
@RequestMapping("/api/v1.0/tweets")
@CrossOrigin(origins = "http://localhost:4200")
public class TweetAppController {

	@Autowired
	TweetApplicationServiceImpl tweetAppService;

	// Register a new user
	@PostMapping(value = "/register")
	public ResponseEntity<?> registerNewUser(@RequestBody UserDetails newUser) {

		try {
			tweetAppService.registerNewUser(newUser);
			return new ResponseEntity<>(newUser, HttpStatus.OK);
		} catch (UserExistsException e) {
			return new ResponseEntity<>(new ErrorResponse("Conflict", "Given userId/email already exists"),
					HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// User login service
	@PostMapping(value = "/login")
	public ResponseEntity<?> loginUser(@RequestBody UserDetails userLoginDetails) {

		try {
			return new ResponseEntity<>(tweetAppService.validateUser(userLoginDetails), HttpStatus.OK);
		} catch (PasswordMismatchException e) {
			return new ResponseEntity<>(new ErrorResponse("Unauthorized", "Mismatch in password provided"),
					HttpStatus.UNAUTHORIZED);
		} catch (UserNotRegisteredException e) {
			return new ResponseEntity<>(new ErrorResponse("User not found", "User with given credentials not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Method to change password
	@PutMapping(value = "/{username}/changePassword")
	public ResponseEntity<?> changePassword(@PathVariable("username") String loginId,
			@RequestHeader(value="newPassword") String newPassword) {
		try {
			return new ResponseEntity<>(tweetAppService.changePassword(loginId, newPassword), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Serive error", "Unable to change password"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Method to retrieve all tweets
	@GetMapping(value = "/all")
	public ResponseEntity<?> getAllTweets() {

		try {
			return new ResponseEntity<>(tweetAppService.getAllTweets(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Method to retrieve all users list
	@GetMapping(value = "/users/all")
	public ResponseEntity<?> getAllUsers() {
		return new ResponseEntity<>(tweetAppService.getAllUsers(), HttpStatus.OK);

	}

	
	// Method to get a user's tweets
	@GetMapping(value = "/{username}")
	public ResponseEntity<?> getUserTweets(@PathVariable("username") String username) {
		try {
			return new ResponseEntity<>(tweetAppService.getUserTweets(username), HttpStatus.OK);
		} catch (InvalidUsernameException e) {
			return new ResponseEntity<>(new ErrorResponse("Unprocessable", "Invalid User param received"),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// Method to post a new tweet
	@PostMapping(value = "/{username}/add")
	public void postNewTweet(@PathVariable("username") String username, @RequestBody Tweet newTweet) {
		tweetAppService.postNewTweet(username, newTweet);
	}

	// Method to update a tweet
	@PutMapping(value = "/{username}/update")
	public ResponseEntity<?> updateTweet(@PathVariable("username") String userId, @RequestHeader( value="tweetId" ) String tweetId,
			@RequestBody Tweet updatedTweet) {
		try {
			return new ResponseEntity<>(tweetAppService.updateTweet(userId, tweetId, updatedTweet), HttpStatus.OK);
		} catch (TweetDoesNotExistException e) {
			return new ResponseEntity<>(new ErrorResponse("Tweet not found", "Given tweetId cannot be found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Method to delete a tweet
	@DeleteMapping(value = "/{username}/delete")
	public ResponseEntity<?> deleteTweet( @PathVariable("username") String userId, 
			@RequestHeader(value = "tweetId") String tweetId) {
		try {
			return new ResponseEntity<>(tweetAppService.deleteTweet(tweetId), HttpStatus.OK);
		} catch (TweetDoesNotExistException e) {
			return new ResponseEntity<>(new ErrorResponse("Tweet not found", "Given tweetId cannot be found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	// Post tweet comment
	@PostMapping(value = "/{username}/reply")
	public ResponseEntity<?> replyToTweet(@PathVariable("username") String userId,
			@RequestHeader(value = "tweetId") String tweetId, @RequestBody Tweet tweetReply) {
		try {
			return new ResponseEntity<>(tweetAppService.replyTweet(userId, tweetId, tweetReply), HttpStatus.OK);
		} catch (TweetDoesNotExistException e) {
			return new ResponseEntity<>(new ErrorResponse("Tweet not found", "Given tweetId cannot be found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorResponse("Service Down", "Application has faced an issue"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
