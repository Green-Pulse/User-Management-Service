package com.greenpulse.auth.user_management_service.config;

import com.greenpulse.auth.user_management_service.event.UserRegisteredEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

//    @Bean
//    public ConsumerFactory<String, Object> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094,localhost:9096");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "user-registered-events");
//
//        // avoid classloader issues
//        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
//
//        return new DefaultKafkaConsumerFactory<>(configProps);
//    }

    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> consumerFactory() {
        JsonDeserializer<UserRegisteredEvent> deserializer = new JsonDeserializer<>(UserRegisteredEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9094,kafka-3:9096");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-registered-events");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }


//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
//            ConsumerFactory<String, Object> consumerFactory) {
//
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory
//                = new ConcurrentKafkaListenerContainerFactory<>();
//
//        factory.setConsumerFactory(consumerFactory);
//
//        return factory;
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, UserRegisteredEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}