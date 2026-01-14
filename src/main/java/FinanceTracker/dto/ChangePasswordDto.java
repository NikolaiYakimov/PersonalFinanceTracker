package FinanceTracker.dto;

public record ChangePasswordDto(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
