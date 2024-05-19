package com.basel.security.services;

import com.basel.security.dto.requests.AuthRequest;
import com.basel.security.dto.requests.RegisterRequest;
import com.basel.security.dto.responses.AuthResponse;
import com.basel.security.enums.Role;
import com.basel.security.models.User;
import com.basel.security.repositories.UserRepository;
import com.basel.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        try {
            User user = User.builder()
                    .firstname(registerRequest.getFirstname())
                    .lastname(registerRequest.getLastname())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);

            String jwtToken = jwtUtil.generateToken(user);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            throw e;
        }
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();

            String jwtToken = jwtUtil.generateToken(user);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (Exception e) {
            throw e;
        }
    }

}
