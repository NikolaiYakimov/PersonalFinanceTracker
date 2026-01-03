package FinanceTracker.mapper;

import FinanceTracker.dto.BudgetRequestDTO;
import FinanceTracker.dto.BudgetResponseDTO;
import FinanceTracker.entity.Budget;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class BudgetMapper {

    public Budget toEntity(BudgetRequestDTO dto, User user, Category category) {
        if (dto == null)
            throw new IllegalArgumentException("BudgetRequestDTO cannot be null");

        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");


        LocalDate refDate = (dto.date() != null) ? dto.date() : LocalDate.now();

        var builder = Budget.builder()
                .user(user)
                .category(category)
                .startDate(refDate.withDayOfMonth(1))
                .endDate(refDate.withDayOfMonth(refDate.lengthOfMonth()));

        if (dto.amount() != null) {
            builder.limitAmount(dto.amount());
        }
        return builder.build();
    }

    public BudgetResponseDTO toDto(Budget budget, BigDecimal spentAmount) {
        if (budget == null)
            throw new IllegalArgumentException("Budget cannot be null");

        BigDecimal safeSpent = (spentAmount != null) ? spentAmount : BigDecimal.ZERO;

        BigDecimal limit = budget.getLimitAmount();
        BigDecimal remaining = limit.subtract(safeSpent);

        double percentage = (limit.compareTo(BigDecimal.ZERO) > 0)
                ? safeSpent.doubleValue() / limit.doubleValue() * 100.0
                : 0.0;


        return new BudgetResponseDTO(
                budget.getId(),
                limit,
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                safeSpent,
                remaining,
                percentage

        );
    }

}
