package com.tweetapp.TweetApplication.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.TweetApplication.exception.LogEventException;
import com.tweetapp.TweetApplication.model.Tweet;
import com.tweetapp.TweetApplication.model.UserDetails;

@Component
public class TweetAppProducer {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	ObjectMapper objectMapper;

	private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(TweetAppProducer.class);

	public void logUserRegistrationEvents(UserDetails newUser)  {

		try {
			String newUserString = objectMapper.writeValueAsString(newUser);

			ProducerRecord<String, String> newUserProducerRecord = new ProducerRecord<>("UserRegistrationEvent",
					newUser.getLoginId(), newUserString);

			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(newUserProducerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("User K:{} V:{} registration event successfully added to broker", newUser.getLoginId(),
							newUserString);
				}

				@Override
				public void onFailure(Throwable ex) {
					log.info("User registration event not added to broker");
				}
			});

		} catch (JsonProcessingException e) {
			log.info("Error while posting to kafka broker.");
		}

	}

	public void logPasswordChangeEvents(UserDetails updatedDetails) throws LogEventException {

		try {
			String updatedDetailsString = objectMapper.writeValueAsString(updatedDetails);
			
			ProducerRecord<String, String> producerRecord = new ProducerRecord<>("PasswordChangeEvent",
					updatedDetails.getLoginId(),
					"updatedDetails: " + updatedDetailsString);

			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onFailure(Throwable ex) {
					log.info("Error while posting password change event to broker");
				}

				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("User {} has changed password to {}", result.getProducerRecord().key(),
							 updatedDetails.getPassword());

				}
			});

		} catch (JsonProcessingException e) {
			log.info("Error while posting to kafka broker.");
		}

	}

	public void logNewPostEvents(Tweet newTweet) {

		try {
			String newTweetString = objectMapper.writeValueAsString(newTweet);

			ProducerRecord<String, String> producerRecord = new ProducerRecord<>("NewPostEvent", newTweet.getTweetId(),
					newTweetString);

			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("User {} has posted a new message: {}", newTweet.getUserId(), newTweet.getTweetText());
				}

				@Override
				public void onFailure(Throwable ex) {
					log.info("Logging of new post event failed for user {}. Post: {}", newTweet.getUserId(),
							newTweet.getTweetText());
				}
			});

		} catch (JsonProcessingException e) {
			log.info("Error while posting to kafka broker.");
		}

	}

	public void logEditPostEvents(Tweet editedTweet, Tweet originalTweet) {

		try {
			String editedTweetString = objectMapper.writeValueAsString(editedTweet);
			String originalTweetString = objectMapper.writeValueAsString(originalTweet);

			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("EditPostEvent",
					originalTweet.getTweetId(),
					"editedTweet: " + editedTweetString + " ; " + "originalTweet: " + originalTweetString);

			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("User {} edited post with tweetId {} recently. original post: {}, new post: {}",
							originalTweet.getUserId(), originalTweetString, editedTweetString);

				}

				@Override
				public void onFailure(Throwable ex) {
					log.info("Error while logging edit post event for user {}. Tweet id: {}", originalTweet.getUserId(),
							originalTweet.getTweetId());
				}
			});

		} catch (JsonProcessingException e) {
			log.info("Error while posting to kafka broker.");
		}

	}

	public void logDeletePostEvents(Tweet postToDelete) {

		try {
			String postToDeleteString = objectMapper.writeValueAsString(postToDelete);

			ProducerRecord<String, String> producerRecord = new ProducerRecord<>("DeletePostEvent",
					postToDelete.getTweetId(), postToDeleteString);

			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("User {} deleted post with id: {}", postToDelete.getUserId(), postToDelete.getTweetId());
				}

				@Override
				public void onFailure(Throwable ex) {
					log.info("Error while logging delete post event for user {}. Deleted post id: {}",
							postToDelete.getUserId(), postToDelete.getTweetId());

				}
			});
		} catch (JsonProcessingException e) {
			log.info("Error while posting to kafka broker.");
		}

	}

}
