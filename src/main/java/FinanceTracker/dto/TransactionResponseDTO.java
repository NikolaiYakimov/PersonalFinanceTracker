package FinanceTracker.dto;

import FinanceTracker.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        BigDecimal amount,
        String description,
        LocalDateTime time,
        TransactionType type,
        Long categoryId,
        String categoryName,
        String currencyCode,
        String currencySymbol
) {
}
