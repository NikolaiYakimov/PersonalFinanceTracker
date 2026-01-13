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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    //Get active budgets
    public List<BudgetResponseDTO> getMyActiveBudgets(Long userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findAllActiveBudgets(userId, now);

        return budgets.stream()
                .map(this::mapToDtoWithSpentAmount)
                .toList();
    }

    public List<BudgetResponseDTO> getMyBudgetHistory(Long userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findAllPastBudgets(userId, now);

        return budgets.stream()
                .map(this::mapToDtoWithSpentAmount)
                .toList();
    }

    public List<BudgetResponseDTO> getPastAndActiveBudgets(Long userId) {
        List<Budget> budgets=budgetRepository.findByUserId(userId);

        return budgets.stream()
                .map(this::mapToDtoWithSpentAmount)
                .toList();
    }


    public BudgetResponseDTO getBudgetById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget Not Found"));

        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized! This budget is not yours");
        }
        return mapToDtoWithSpentAmount(budget);
    }


    public boolean willTransactionExceedBudget(Long userId, Long categoryId, BigDecimal transactionAmount, LocalDate transactionDate) {
        var budgetOptional = budgetRepository.findActiveBudgetByCategory(userId, categoryId, transactionDate);

        if (budgetOptional.isEmpty()) {
            return false;
        }
        Budget budget = budgetOptional.get();

        BigDecimal currSpent = getSpentAmountForBudget(budget);
        BigDecimal totalAfterTransaction = currSpent.add(transactionAmount);

        return totalAfterTransaction.compareTo(budget.getLimitAmount()) > 0;

    }


    @Transactional
    public BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User is not found"));

        Category category = categoryRepository.findById(budgetRequestDTO.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        boolean hasOverlap = budgetRepository.existOverlappingBudget(userId, budgetRequestDTO.categoryId(), budgetRequestDTO.startDate(), budgetRequestDTO.endDate());

        if (hasOverlap) {
            throw new RuntimeException("Budget already exists for this category on this date!");
        }

        Budget budget = budgetMapper.toEntity(budgetRequestDTO, user, category);
        Budget savedBudget=budgetRepository.save(budget);

        return budgetMapper.toDto(savedBudget,getSpentAmountForBudget(budget));
    }

    @Transactional
    public BudgetResponseDTO updateBudget(Long id, BudgetRequestDTO budgetRequestDTO, Long userId) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget is not found"));

        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized! This budget is not yours");
        }

        boolean isCategoryChanged = !budget.getCategory().getId().equals(budgetRequestDTO.categoryId());
        boolean areDateChanged = !budget.getStartDate().equals(budgetRequestDTO.startDate()) ||
                !budget.getEndDate().equals(budgetRequestDTO.endDate());

        if (isCategoryChanged || areDateChanged) {
            boolean overlap = budgetRepository.existOverlappingBudgetExcludingCurrent(userId,
                    budgetRequestDTO.categoryId(),
                    budgetRequestDTO.startDate(),
                    budgetRequestDTO.endDate(),
                    id);

            if (overlap) {
                throw new RuntimeException("Updated budget overlaps with another existing budget!");
            }

        }

        budget.setLimitAmount(budgetRequestDTO.amount());
        budget.setStartDate(budgetRequestDTO.startDate());
        budget.setEndDate(budgetRequestDTO.endDate());
        if (isCategoryChanged) {
            Category newCategory = categoryRepository.findById(budgetRequestDTO.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category is not found"));
            budget.setCategory(newCategory);
        }

        Budget updatedBudget = budgetRepository.save(budget);
        BigDecimal spent = getSpentAmountForBudget(updatedBudget);
        return budgetMapper.toDto(updatedBudget, spent);
    }

    @Transactional
    public void deleteBudget(Long id, Long userId) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget is not found"));

        if (!budget.getUser().getId().equals(userId))
            throw new RuntimeException("Unauthorized! This budget is not yours");

        budgetRepository.delete(budget);
    }
    private BudgetResponseDTO mapToDtoWithSpentAmount(Budget budget) {
        BigDecimal spent = getSpentAmountForBudget(budget);
        return budgetMapper.toDto(budget, spent);
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
