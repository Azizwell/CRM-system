package org.example.crm_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientDto(
        @NotBlank
        String name,
        @NotBlank
        String phone,
        @NotNull
        Long serviceTypeId

) {
}
