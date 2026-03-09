package com.iglesia.controller;

import com.iglesia.dto.request.CreateUserRequest;
import com.iglesia.dto.response.UserResponse;

import com.iglesia.UserRole;
import com.iglesia.entity.AppUser;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.repository.AppUserRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponse createClient(@Valid @RequestBody CreateUserRequest request) {
        if (appUserRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("El email ya está registrado");
        }

        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CLIENT);

        appUserRepository.save(user);
        return UserResponse.from(user);
    }

}