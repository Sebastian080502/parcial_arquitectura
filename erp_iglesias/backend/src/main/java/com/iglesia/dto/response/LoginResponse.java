package com.iglesia.dto.response;

public record LoginResponse(
    String token,
    String email,
    String role
) {}