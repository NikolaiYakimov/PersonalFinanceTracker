package FinanceTracker.controller;

import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody @Valid TransactionRequestDTO dto){
        return ResponseEntity.ok(transactionService.createTransaction(dto));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        if(startDate!=null && endDate!=null){
            return ResponseEntity.ok(transactionService.getTransactionsByDateRange(startDate, endDate));
        }
        return ResponseEntity.ok(transactionService.getMyTransactions());
    }

}
