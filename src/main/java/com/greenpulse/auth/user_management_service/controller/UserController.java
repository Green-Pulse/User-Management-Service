package com.greenpulse.auth.user_management_service.controller;

import com.greenpulse.auth.user_management_service.dto.UserDto;
import com.greenpulse.auth.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

}
