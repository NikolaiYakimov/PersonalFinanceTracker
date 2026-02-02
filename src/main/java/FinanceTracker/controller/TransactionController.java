package FinanceTracker.controller;

import FinanceTracker.dto.CategorySumDTO;
import FinanceTracker.dto.DashboardStatsDTO;
import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.enums.TransactionType;
import FinanceTracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transactionService.getTransactions(startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody @Valid TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createTransaction(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(@PathVariable Long id, @RequestBody @Valid TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsPaged(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponseDTO>> searchTransactions(@RequestParam String keyword) {
        return ResponseEntity.ok(transactionService.searchTransactions(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(transactionService.getTransactionByCategory(categoryId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatus() {
        return ResponseEntity.ok(transactionService.getDashboardStats());
    }

    @GetMapping("/spending-by-category")
    public ResponseEntity<List<CategorySumDTO>> getSpendingByCategory() {
        return ResponseEntity.ok(transactionService.getSpendingByCategory());
    }

    @GetMapping("/total/type")
    public ResponseEntity<BigDecimal> getTotalAmountByTypeAndDate(
            @RequestParam TransactionType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(transactionService.getTotalAmountByTypeAndDate(type, startDate, endDate));
    }

    @GetMapping("/total/category/{categoryId}")
    public ResponseEntity<BigDecimal> gotTotalByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(transactionService.getTotalByCategoryId(categoryId));
    }

    @GetMapping("/search-by-type")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByType(@RequestParam TransactionType type)
    {
        return ResponseEntity.ok(transactionService.getTransactionByType(type));
    }
}
