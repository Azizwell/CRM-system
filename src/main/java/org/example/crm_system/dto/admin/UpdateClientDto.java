package org.example.crm_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateClientDto(
        @NotBlank
        String name,
        @NotBlank
        String phone,
        @NotNull
        Long serviceTypeId,
        @NotNull
        String clientId

) {
}
