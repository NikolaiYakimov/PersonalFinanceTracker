package FinanceTracker.service;

import FinanceTracker.dto.BudgetRequestDTO;
import FinanceTracker.dto.BudgetResponseDTO;
import FinanceTracker.entity.Budget;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.User;
import FinanceTracker.mapper.BudgetMapper;
import FinanceTracker.repository.BudgetRepository;
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.TransactionRepository;
import FinanceTracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    //Get active budgets
    public List<BudgetResponseDTO> getMyBudgets(Long userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findAllActiveBudgets(userId, now);

        return budgets.stream()
                .map(budget -> {
                    BigDecimal spent = getSpentAmountForBudget(budget);
                    return budgetMapper.toDto(budget, spent);
                })
                .toList();
    }

    @Transactional
    public void createBudget(BudgetRequestDTO budgetRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User is not found"));

        Category category = categoryRepository.findById(budgetRequestDTO.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        boolean hasOverlap = budgetRepository.existOverlappingBudget(userId, budgetRequestDTO.categoryId(), budgetRequestDTO.startDate(), budgetRequestDTO.endDate());

        if (hasOverlap) {
            throw new RuntimeException("Budget already exists for this category on this date!");
        }

        Budget budget = budgetMapper.toEntity(budgetRequestDTO, user, category);
        budgetRepository.save(budget);

    }

    @Transactional
    public void deleteBudget(Long id,Long userId){
        Budget budget=budgetRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Budget is not found"));

        if(!budget.getUser().getId().equals(userId))
            throw new RuntimeException("Unauthorized! This budget is not yours");

        budgetRepository.delete(budget);
    }

    private BigDecimal getSpentAmountForBudget(Budget budget) {
        LocalDateTime start = budget.getStartDate().atStartOfDay();
        LocalDateTime end = budget.getEndDate().atTime(23, 59, 59);

        BigDecimal total = transactionRepository.sumTotalByCategoryIdAndDate(
                budget.getUser().getId(),
                budget.getCategory().getId(),
                start,
                end
        );
        return (total != null) ? total : BigDecimal.ZERO;
    }

}
