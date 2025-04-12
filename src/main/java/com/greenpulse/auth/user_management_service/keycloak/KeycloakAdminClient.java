package com.greenpulse.auth.user_management_service.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class KeycloakAdminClient {

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

    public String getAdminAccessToken() {
        String url = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=password&client_id=admin-cli" +
                "&username=" + adminUsername +
                "&password=" + adminPassword;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error getting access token", e);
        }
    }

    public ResponseEntity<String> getAllUsers() {
        String token = getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }


    public String getUserIdByUsername(String username) {
        ResponseEntity<String> response = getUserByUsername(username);
        try {
            JsonNode array = objectMapper.readTree(response.getBody());
            if (array.isArray() && array.size() > 0) {
                return array.get(0).get("id").asText();
            }
            throw new RuntimeException("User not found by username: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user ID from response", e);
        }
    }

    public void updateUserByUsername(String username, Map<String, Object> updates) {
        String userId = getUserIdByUsername(username);
        String token = getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public void deleteUserByUsername(String username) {
        String userId = getUserIdByUsername(username);
        String token = getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public String getCurrentUserIdFromToken(Jwt jwt) {
        return jwt.getSubject(); // обычно — UUID юзера
    }

    public ResponseEntity<String> getUserByUsername(String username) {
        String token = getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
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

