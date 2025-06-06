package com.greenpulse.auth.user_management_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").authenticated()  // Только авторизованные
                        .anyRequest().permitAll() // Остальное — доступно всем
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)) // Используем JWT из Keycloak
                );
        return http.build();
    }
}
