package hub.com.apireports.dto.security;

import hub.com.apireports.entity.security.RoleType;
import hub.com.apireports.entity.security.UserStatus;

public record MemberDTOResponse(
        Long id,
        String name,
        String lastName,
        String dni,
        String phone,
        String email,
        String username,
        String password,
        RoleType role,
        UserStatus status
) { }
