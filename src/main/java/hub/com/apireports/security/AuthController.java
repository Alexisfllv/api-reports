package hub.com.apireports.security;


import hub.com.apireports.dto.security.AuthResponse;
import hub.com.apireports.dto.security.LoginRequest;
import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.dto.security.RegisterRequest;
import hub.com.apireports.service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// #9
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService  authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerPost(@RequestBody RegisterRequest registerRequest){
        AuthResponse res =  authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginPost(@RequestBody LoginRequest loginRequest){
        AuthResponse res =  authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
