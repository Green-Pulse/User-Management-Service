package com.greenpulse.auth.user_management_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserCreatedEvent(String userJson) {
        kafkaTemplate.send(KafkaTopics.USER_CREATED_TOPIC, userJson);
        log.info("Sent user to Kafka: {}", userJson);
    }
}
