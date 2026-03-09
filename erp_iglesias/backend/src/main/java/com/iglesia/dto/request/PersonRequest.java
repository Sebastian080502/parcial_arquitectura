package com.iglesia.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PersonRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String document,
    String phone,
    String email
) {}