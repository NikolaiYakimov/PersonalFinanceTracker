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

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate

) {
    public BudgetRequestDTO {
        if (startDate != null && endDate != null && endDate.isBefore(startDate))
        {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
