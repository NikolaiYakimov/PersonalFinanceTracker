package FinanceTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionRequestDTO(
        @NotNull(message = "The amount is required")
        @Positive(message = "The amount must be positive")
        BigDecimal amount,

        String description,

        @NotNull(message = "Date is required")
        LocalDateTime date,

        @NotNull(message = "Category ID is required")
        Long categoryId,

//        @NotNull(message = "Currency code is required")
        String currencyCode
) {
}
