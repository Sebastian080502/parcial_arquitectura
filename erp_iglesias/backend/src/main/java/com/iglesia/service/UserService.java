package com.iglesia.service;

import com.iglesia.entity.AppUser;
import com.iglesia.UserRole;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.repository.AppUserRepository;
import com.iglesia.dto.request.CreateUserRequest;
import com.iglesia.dto.response.UserResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createClient(CreateUserRequest request) {
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