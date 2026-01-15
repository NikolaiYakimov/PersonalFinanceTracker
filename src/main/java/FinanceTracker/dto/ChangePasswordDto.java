package FinanceTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank(message = "Current password is required")
        String currentPassword,
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters long")
        String newPassword,
        @NotBlank(message = "Confirmation password is required")
        String confirmPassword
) {
}
