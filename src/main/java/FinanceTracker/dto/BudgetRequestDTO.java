package FinanceTracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequestDTO(
        @NotNull(message = "Category is required")
        Long categoryId,

        @Positive(message = "Limit amount must be positive")
        BigDecimal amount,

        LocalDate date

        ) {
}
