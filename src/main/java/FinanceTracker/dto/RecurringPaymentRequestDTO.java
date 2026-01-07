package FinanceTracker.dto;

import FinanceTracker.enums.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecurringPaymentRequestDTO(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,


        @NotBlank(message = "Name is required")
        String description,

        @NotNull(message="Category is required")
        Long categoryId,

        @NotNull(message = "Start date is required")
        LocalDateTime startDate,

        @NotNull(message = "Frequency is required ( DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY )")
        Frequency frequency,

        String currencyCode
) {
}
