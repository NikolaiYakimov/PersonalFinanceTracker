package FinanceTracker.dto;

import FinanceTracker.enums.Role;

public record AuthResponseDTO(
        String token,
        String username,
        Role role
)
{}
