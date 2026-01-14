package FinanceTracker.service;

import FinanceTracker.dto.CategorySumDTO;
import FinanceTracker.dto.DashboardStatsDTO;
import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.Transaction;
import FinanceTracker.entity.User;
import FinanceTracker.enums.TransactionType;
import FinanceTracker.mapper.TransactionMapper;
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.CurrencyRepository;
import FinanceTracker.repository.TransactionRepository;
import FinanceTracker.repository.UserRepository;
import FinanceTracker.security.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final CurrencyRepository currencyRepository;
    private final BudgetService budgetService;
    private final UserHelper userHelper;


    public List<TransactionResponseDTO> getMyTransactions() {
        User user=userHelper.getCurrentUser();
        return transactionRepository.findByUserIdOrderByDateDesc(user.getId()).stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public Page<TransactionResponseDTO> getTransactionsPaged(Long userId, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        User user=userHelper.getCurrentUser();

        return transactionRepository.findByUserIdOrderByDateDesc(user.getId(),pageable).map(transactionMapper::toDto);
    }

    public TransactionResponseDTO getTransactionById(Long transactionId) {
        User user=userHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized! This transaction is not yours");
        }

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionResponseDTO> getTransactionsByDateRange( LocalDate startDate,LocalDate endDate){
        User user=userHelper.getCurrentUser();
        LocalDateTime start=startDate.atStartOfDay();
        LocalDateTime end=endDate.atTime(23,59,59);

        return transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(),start,end)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public List<TransactionResponseDTO> searchTransactions(String keyword) {
        User user=userHelper.getCurrentUser();
        return transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(user.getId(), keyword)
                .stream()
                .map(transactionMapper::toDto)
                .toList();

    }

    public List<TransactionResponseDTO> getTransactionByCategory(Long categoryId) {
        User user=userHelper.getCurrentUser();
        return transactionRepository.findByUserIdAndCategoryIdOrderByDateDesc(user.getId(), categoryId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();

    }

    public List<TransactionResponseDTO> getTransactionByType(String typeStr) {
        User user=userHelper.getCurrentUser();
        TransactionType transactionType = TransactionType.valueOf(typeStr.toUpperCase());
        return transactionRepository.findByUserIdAndCategory_Type(user.getId(), transactionType)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionDTO) {

        User user=userHelper.getCurrentUser();

        Category category = categoryRepository.findById(transactionDTO.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));


        //If category have owner , and the owner is not you
        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot make transaction to categories that is not yours");
        }

        String code = (transactionDTO.currencyCode() != null) ? transactionDTO.currencyCode() : "EUR";
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + code));

        boolean isLimitExceeded = budgetService.willTransactionExceedBudget(
                user.getId(),
                category.getId(),
                transactionDTO.amount(),
                transactionDTO.date().toLocalDate());

        if (isLimitExceeded) {
            throw new RuntimeException("Warning: This transaction exceeds your budget limit for " + category.getName() + "!");
        }

        Transaction transaction = transactionMapper.toEntity(transactionDTO, user, category, currency);
       Transaction savedTransaction= transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    public TransactionResponseDTO updateTransaction(Long transactionId, TransactionRequestDTO dto) {
        User user=userHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ( !transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot make transaction to categories that is not yours");
        }

        if ( !transaction.getCategory().getId().equals(dto.categoryId())) {
            Category newCategory = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (newCategory.getUser() != null && !newCategory.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Invalid Category");
            }

            transaction.setCategory(newCategory);
        }

        if (!transaction.getCurrency().getCode().equals(dto.currencyCode())) {
            Currency currency = currencyRepository.findByCode(dto.currencyCode())
                    .orElseThrow(() -> new RuntimeException("Currency not found"));

            transaction.setCurrency(currency);
        }

        boolean isLimitExceeded = budgetService.willTransactionExceedBudget(
                user.getId(),
                transaction.getCategory().getId(),
                dto.amount(),
                transaction.getDate().toLocalDate()
        );
        if (isLimitExceeded) {
            throw new RuntimeException("Warning: Updated transaction exceeds budget limit!");
        }

        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setDate(dto.date());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId) {
        User user=userHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot delete transaction that is not yours");
        }

        transactionRepository.delete(transaction);
    }

    //Can move to different service if i get more method about statistic like that
    public DashboardStatsDTO getDashboardStats() {
        User user=userHelper.getCurrentUser();
        BigDecimal totalIncome = transactionRepository.sumTotalAmountByType(user.getId(), TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumTotalAmountByType(user.getId(), TransactionType.EXPENSE);

        totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;

        totalExpenses = (totalExpenses != null) ? totalExpenses : BigDecimal.ZERO;

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new DashboardStatsDTO(totalIncome, totalExpenses, balance);

    }

    public List<CategorySumDTO> getSpendingByCategory() {
        User user=userHelper.getCurrentUser();
        return transactionRepository.findGroupByCategory(user.getId(), TransactionType.EXPENSE);
    }
}
