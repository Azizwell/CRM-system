package org.example.crm_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddUserDto(
        @NotBlank
        String fullName,
        @NotNull
        String password,
        @NotBlank
        String username,
        @NotBlank
        String rolId
) {
}
