package com.iglesia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CourseRequest(
    @NotBlank String name,
    String description,
    @NotNull BigDecimal price
) {}