package com.greenpulse.auth.user_management_service.service.impl;

import com.greenpulse.auth.user_management_service.repository.RoleRepository;
import com.greenpulse.auth.user_management_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.auth.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;


}
