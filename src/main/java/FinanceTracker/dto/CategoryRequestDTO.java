package FinanceTracker.dto;

import FinanceTracker.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(

        @Size(min = 3,message = "Name of category must be at least 3 characters")
        @NotBlank(message = "Category name is required")
        String name,

        @NotNull(message = "Type is required( INCOME OR EXPENSE)")
        TransactionType type
) {
}
