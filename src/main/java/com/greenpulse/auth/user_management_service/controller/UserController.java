package com.greenpulse.auth.user_management_service.controller;

import com.greenpulse.auth.user_management_service.dto.UserDto;
import com.greenpulse.auth.user_management_service.keycloak.KeycloakAdminClient;
import com.greenpulse.auth.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakAdminClient keycloakAdminClient;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> getUsers() {
        return keycloakAdminClient.getAllUsers();
    }

    @PutMapping("/by-username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> updateUserByUsername(
            @PathVariable String username,
            @RequestBody Map<String, Object> updates) {
        keycloakAdminClient.updateUserByUsername(username, updates);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        keycloakAdminClient.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = keycloakAdminClient.getCurrentUserIdFromToken(jwt);
        return keycloakAdminClient.getUserByUsername(jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username) {
        return keycloakAdminClient.getUserByUsername(username);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        keycloakAdminClient.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

}
