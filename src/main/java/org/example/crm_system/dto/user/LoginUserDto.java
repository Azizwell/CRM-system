package org.example.crm_system.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(
        @NotBlank
        String password,
        @NotBlank
        String username
) {
}
