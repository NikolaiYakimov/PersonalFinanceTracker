package FinanceTracker.service;

import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.Transaction;
import FinanceTracker.entity.User;
import FinanceTracker.enums.TransactionType; // ВАЖНО: Импорт на Enum-а
import FinanceTracker.mapper.TransactionMapper;
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.CurrencyRepository;
import FinanceTracker.repository.TransactionRepository;
import FinanceTracker.security.UserHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private TransactionMapper transactionMapper;
    @Mock private UserHelper userHelper;
    @Mock private BudgetService budgetService; // Трябва, защото createTransaction го вика

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_ShouldSaveAndReturnDto() {

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(10L);
        category.setName("Food");
        category.setType(TransactionType.EXPENSE);
        category.setUser(user); // Категорията е на същия юзър


        Currency currency = new Currency();
        currency.setCode("BGN");
        currency.setSymbol("лв");


        TransactionRequestDTO request = new TransactionRequestDTO(
                new BigDecimal("50.00"),
                "Lunch",
                LocalDateTime.now(),
                10L,   // categoryId
                "BGN"  // currencyCode
        );

        Transaction transactionEntity = new Transaction();
        transactionEntity.setId(100L);
        transactionEntity.setAmount(new BigDecimal("50.00"));
        transactionEntity.setDescription("Lunch");
        transactionEntity.setCategory(category);
        transactionEntity.setCurrency(currency);

        TransactionResponseDTO expectedResponse = new TransactionResponseDTO(
                100L,
                new BigDecimal("50.00"),
                "Lunch",
                LocalDateTime.now(),
                TransactionType.EXPENSE,
                10L,
                "Food",
                "BGN",
                "лв"
        );



        when(userHelper.getCurrentUser()).thenReturn(user);

        //
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        when(currencyRepository.findByCode("BGN")).thenReturn(Optional.of(currency));

        when(budgetService.willTransactionExceedBudget(any(), any(), any(), any())).thenReturn(false);

        when(transactionMapper.toEntity(request, user, category, currency)).thenReturn(transactionEntity);

        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);

        when(transactionMapper.toDto(transactionEntity)).thenReturn(expectedResponse);

        TransactionResponseDTO actualResponse = transactionService.createTransaction(request);

        assertNotNull(actualResponse);
        assertEquals(new BigDecimal("50.00"), actualResponse.amount());
        assertEquals("Food", actualResponse.categoryName());
        assertEquals("лв", actualResponse.currencySymbol());

        verify(transactionRepository).save(transactionEntity);
    }
}