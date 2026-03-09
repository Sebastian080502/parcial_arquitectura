package com.iglesia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OfferingRequest(
    @NotNull Long personId,
    @NotNull BigDecimal amount,
    @NotBlank String concept
) {}