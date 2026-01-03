package FinanceTracker.dto;

import FinanceTracker.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDTO(


        @NotBlank(message = "Category name is required")
        String name,

        @NotNull(message = "Type is required( INCOME OR EXPENSE)")
        TransactionType type
) {
}
