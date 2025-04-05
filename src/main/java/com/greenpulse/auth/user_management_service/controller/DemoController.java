package com.greenpulse.auth.user_management_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class DemoController {

    @GetMapping("/demohello")
    public String sayHello() {
        return "Hello, authenticated user!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-secured")
    public String adminOnly() {
        return "Welcome, admin!";
    }
}
