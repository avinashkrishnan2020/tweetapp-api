package com.tweetapp.TweetApplication.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TweetAppConsumer {

	org.apache.logging.log4j.Logger log = LogManager.getLogger(TweetAppConsumer.class);

	@KafkaListener(topics = { "UserRegistrationEvent" })
	public void listenUserRegistrationEvents(ConsumerRecord<String, String> recordFromTopic) {
		try {
			log.info("Consumer fetched record: {}", recordFromTopic.value());
		} catch (Exception e) {
			log.info("Error while reading record from topic");
		}
	}
	
	@KafkaListener(topics = {"PasswordChangeEvent"})
	public void listenPasswordChangeEvents(ConsumerRecord<String, String> recordFromTopic) {
		try {
			log.info("User {} has changed password", recordFromTopic.key());
		} catch (Exception e) {
			log.info("Error while reading record from topic");
		}
	}
	
	@KafkaListener(topics= {"NewPostEvent"}) 
	public void listenNewPostEvents(ConsumerRecord<String, String> recordFromTopic) {
		try {
			log.info("user {} has posted a new tweet: {}", recordFromTopic.key(), recordFromTopic.value());
		} catch (Exception e) {
			log.info("Error while reading record from topic");
		}
	}
	
	@KafkaListener(topics = {"EditPostEvent"})
	public void listenEditPostEvents(ConsumerRecord<String, String> recordFromTopic) {
		try {
			log.info("User {} has edited a post: {}",recordFromTopic.key(), recordFromTopic.value());
		} catch (Exception e) {
			log.info("Error while reading record from topic");
		}
	}
	
	@KafkaListener(topics = {"DeletePostEvent"})
	public void listenDeletePostEvents(ConsumerRecord<String, String> recordFromTopic) {
		try {
			log.info("user {} has deleted a post", recordFromTopic.key());
		} catch (Exception e) {
			log.info("Error while reading record from topic");
		}
	}
	
}
