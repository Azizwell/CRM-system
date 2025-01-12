package org.example.crm_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseDto(
        @NotNull
        String type,
        @NotNull
        BigDecimal amount,
        @NotBlank
        String currency,
        @NotNull
        Long categoryId,
        String description,
        LocalDate date
) {
}
