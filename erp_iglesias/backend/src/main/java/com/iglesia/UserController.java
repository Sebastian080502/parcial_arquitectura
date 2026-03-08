package com.iglesia;

import com.iglesia.AppUser;
import com.iglesia.UserRole;
import com.iglesia.exception.BusinessRuleException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    // DTOs internos
    public record CreateUserRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record UserResponse(Long id, String email, String role) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getEmail(), user.getRole().name());
        }
    }
}