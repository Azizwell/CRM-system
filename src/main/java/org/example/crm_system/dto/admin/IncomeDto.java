package org.example.crm_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IncomeDto(
        @NotNull
        String type,
        @NotNull
        BigDecimal amount,
        @NotBlank
        String currency,
        @NotBlank
        String status,
        @NotBlank
        String clientId,
        String description,
        LocalDate date
) {
}
