package com.greenpulse.auth.user_management_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // хэширует пароли
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); // нужен для Kafka и сериализации DTO
    }
}
