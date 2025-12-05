package FinanceTracker.dto;

import java.math.BigDecimal;

public interface CategorySumDTO {
    String getCategoryName();
    BigDecimal getTotalAmount();
}
