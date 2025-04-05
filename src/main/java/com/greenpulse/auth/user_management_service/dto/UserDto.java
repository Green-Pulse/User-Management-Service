package com.greenpulse.auth.user_management_service.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String status;
    private Set<String> roles;
}
