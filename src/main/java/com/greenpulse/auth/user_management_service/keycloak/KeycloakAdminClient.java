package com.greenpulse.auth.user_management_service.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.auth.user_management_service.dto.UserDto;
import com.greenpulse.auth.user_management_service.mapper.KeycloakUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final KeycloakUserMapper userMapper;

    @Value("${spring.keycloak.server-url}")
    private String keycloakUrl;

    @Value("${spring.keycloak.realm}")
    private String realm;

    @Value("${spring.keycloak.admin-username}")
    private String adminUsername;

    @Value("${spring.keycloak.admin-password}")
    private String adminPassword;

    @Value("${spring.keycloak.client-id}")
    private String clientId;

    @Value("${spring.keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getCurrentUserIdFromToken(Jwt jwt) {
        return jwt.getSubject();
    }

    public List<UserDto> getAllUsers() {
        String token = userMapper.getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            List<UserDto> users = new ArrayList<>();
            for (JsonNode node : root) {
                users.add(userMapper.toUserDto(node));
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping users", e);
        }
    }

    public UserDto getUserById(String userId) {
        String token = userMapper.getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            return userMapper.toUserDto(node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map user by ID", e);
        }
    }

    public void updateUserByUsername(String username, Map<String, Object> updates) {
        String userId = String.valueOf(getUserByUsername(username).getId());
        String token = userMapper.getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public void deleteUserByUsername(String username) {
        String userId = String.valueOf(getUserByUsername(username).getId());
        String token = userMapper.getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public UserDto getUserByUsername(String username) {
        String token = userMapper.getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        try {
            JsonNode array = objectMapper.readTree(response.getBody());
            if (array.isArray() && array.size() > 0) {
                return userMapper.toUserDto(array.get(0));
            } else {
                throw new RuntimeException("User not found: " + username);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to map user from response", e);
        }
    }


    public void logout(String refreshToken) {
        String url = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

}

