package com.greenpulse.auth.user_management_service.service.impl;

import com.greenpulse.auth.user_management_service.dto.UserDto;
import com.greenpulse.auth.user_management_service.model.Role;
import com.greenpulse.auth.user_management_service.model.User;
import com.greenpulse.auth.user_management_service.kafka.KafkaUserProducer;
import com.greenpulse.auth.user_management_service.repository.RoleRepository;
import com.greenpulse.auth.user_management_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.auth.user_management_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaUserProducer kafkaProducer;
    private final ObjectMapper objectMapper;


}
