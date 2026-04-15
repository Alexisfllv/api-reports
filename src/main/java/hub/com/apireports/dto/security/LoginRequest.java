package hub.com.apireports.dto.security;

public record LoginRequest(
        String username,
        String password
) {}