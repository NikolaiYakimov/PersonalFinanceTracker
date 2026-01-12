package FinanceTracker.dto;

import java.math.BigDecimal;

public record DashboardStatsDTO(
         BigDecimal totalIncome,
         BigDecimal totalExpenses,
         BigDecimal balance
) {
}
