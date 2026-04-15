package hub.com.apireports.service.security;


import hub.com.apireports.dto.security.AuthResponse;
import hub.com.apireports.dto.security.LoginRequest;
import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.dto.security.RegisterRequest;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.entity.security.UserStatus;
import hub.com.apireports.repo.MemberRepo;
import hub.com.apireports.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// #8
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepo memberRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // Register
    public AuthResponse register(RegisterRequest registerRequest) {
        Member  member = new Member();
        member.setName(registerRequest.name());
        member.setLastName(registerRequest.lastName());
        member.setDni(registerRequest.dni());
        member.setPhone(registerRequest.phone());
        member.setEmail(registerRequest.email());
        member.setUsername(generateUsername(registerRequest.name(), registerRequest.lastName(), registerRequest.dni()));
        member.setPassword(passwordEncoder.encode(registerRequest.password()));
        member.setRole(registerRequest.role());
        member.setStatus(UserStatus.ACTIVE);
        memberRepo.save(member);

        return new AuthResponse(null,"Registerded successfully");
    }

    public String generateUsername(String name, String lastName, String dni) {
        return (name.substring(0,1) + lastName.substring(0,1) + dni).toLowerCase();
    }

    // Login
    public AuthResponse login(LoginRequest loginRequest) {
        Member member = memberRepo.findByUsername(loginRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
        return new AuthResponse(jwtUtil.generateToken(member), "Login successful");
    }

    // FindAll
    public List<MemberDTOResponse> findAll() {
        return memberRepo.findAll().stream()
                .map(member -> new MemberDTOResponse(
                        member.getId(),
                        member.getName(),
                        member.getLastName(),
                        member.getDni(),
                        member.getPhone(),
                        member.getEmail(),
                        member.getUsername(),
                        member.getPassword(),
                        member.getRole(),
                        member.getStatus()
                ))
                .toList();
    }
}
