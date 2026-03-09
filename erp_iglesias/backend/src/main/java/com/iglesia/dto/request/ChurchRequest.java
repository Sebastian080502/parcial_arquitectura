package com.iglesia.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChurchRequest(
    @NotBlank String name,
    String address
) {}