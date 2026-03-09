package com.iglesia.controller;

import com.iglesia.JwtService;
import com.iglesia.entity.AppUser;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.repository.AppUserRepository;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.iglesia.dto.request.LoginRequest;
import com.iglesia.dto.response.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getRole().name());
    }

}