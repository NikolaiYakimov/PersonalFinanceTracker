package FinanceTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Size(min = 3, max = 50,message = "Username must be between 3 and 50 characters")
        String username,
        @Email(message = "Invalid email format")
        @Size(max = 100,message = "Email is too long")
        String email
) {
}
