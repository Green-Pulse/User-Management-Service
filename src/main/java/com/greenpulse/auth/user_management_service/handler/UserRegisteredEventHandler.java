package com.greenpulse.auth.user_management_service.handler;

import com.greenpulse.auth.user_management_service.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "user-registered-event-topic")
public class UserRegisteredEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handleUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        LOGGER.info("Received UserRegisteredEvent: {}", userRegisteredEvent.getUsername());
    }
}
