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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakAdminClient keycloakAdminClient;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(keycloakAdminClient.getAllUsers());
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

    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(keycloakAdminClient.getUserByUsername(username));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = keycloakAdminClient.getCurrentUserIdFromToken(jwt);
        return ResponseEntity.ok(keycloakAdminClient.getUserById(userId));
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        keycloakAdminClient.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
}
