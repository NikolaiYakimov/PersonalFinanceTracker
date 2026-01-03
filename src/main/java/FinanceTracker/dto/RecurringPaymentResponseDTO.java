package FinanceTracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringPaymentResponseDTO(
        Long id,
        BigDecimal amount,
        String description,
        String categoryName,
        LocalDate nextPaymentDate,
        boolean isActive,
        String currencyCode,
        String currencySymbol
) {
}
