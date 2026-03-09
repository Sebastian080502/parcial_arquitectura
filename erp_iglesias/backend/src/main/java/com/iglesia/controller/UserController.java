package com.iglesia.controller;

import com.iglesia.service.UserService;
import com.iglesia.dto.request.CreateUserRequest;
import com.iglesia.dto.response.UserResponse;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponse createClient(@Valid @RequestBody CreateUserRequest request) {
        return userService.createClient(request);
    }
}