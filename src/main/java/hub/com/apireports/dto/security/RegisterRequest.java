package hub.com.apireports.dto.security;

import hub.com.apireports.entity.security.RoleType;

// #02
public record RegisterRequest(
        String name,
        String lastName,
        String dni,
        String phone,
        String email,
        String password,
        RoleType role
) {}
