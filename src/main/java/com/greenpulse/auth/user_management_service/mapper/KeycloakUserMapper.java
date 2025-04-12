package com.greenpulse.auth.user_management_service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.auth.user_management_service.dto.UserDto;
import com.greenpulse.auth.user_management_service.keycloak.KeycloakAdminClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class KeycloakUserMapper {

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

    public UserDto toUserDto(JsonNode node) {
        UUID id = UUID.fromString(node.path("id").asText());
        String username = node.path("username").asText();
        String email = node.path("email").asText();
        String status = node.path("enabled").asBoolean() ? "ENABLED" : "DISABLED";

        Set<String> roles = new HashSet<>();
        try {
            roles = getUserRoles(id.toString());
        } catch (Exception e) {
            // Логируем, но не падаем — пусть возвращаются хотя бы ост. данные
            System.err.println("Failed to load roles for user " + id + ": " + e.getMessage());
        }

        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .status(status)
                .roles(roles)
                .build();
    }

    public Set<String> getUserRoles(String userId) {
        String token = getAdminAccessToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        try {
            JsonNode rolesArray = objectMapper.readTree(response.getBody());
            Set<String> roles = new HashSet<>();
            for (JsonNode roleNode : rolesArray) {
                roles.add(roleNode.path("name").asText());
            }
            return roles;
        } catch (Exception e) {
            throw new RuntimeException("Error loading user roles", e);
        }
    }

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
}
