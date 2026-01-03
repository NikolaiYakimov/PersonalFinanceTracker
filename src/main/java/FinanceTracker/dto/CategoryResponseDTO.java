package FinanceTracker.dto;

import FinanceTracker.enums.TransactionType;

public record CategoryResponseDTO(
        Long id,
        String name,
        TransactionType type,
        boolean isCustom
) {}
