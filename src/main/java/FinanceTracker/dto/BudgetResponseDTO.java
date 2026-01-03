package FinanceTracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResponseDTO(
        Long id,
        BigDecimal limitAmount,
        LocalDate startDate,
        LocalDate endDate,

        //Category info
        Long categoryId,
        String categoryName,

        //Additional fields
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double percentageUsed
) {
}
