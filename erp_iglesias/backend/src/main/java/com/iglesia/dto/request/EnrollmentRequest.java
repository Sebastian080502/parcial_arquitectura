package com.iglesia.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
    @NotNull Long personId,
    @NotNull Long courseId
) {}
