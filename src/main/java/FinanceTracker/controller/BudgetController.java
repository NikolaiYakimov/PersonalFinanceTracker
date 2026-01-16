package FinanceTracker.controller;



import FinanceTracker.dto.BudgetRequestDTO;
import FinanceTracker.dto.BudgetResponseDTO;
import FinanceTracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponseDTO>> getActiveBudgets(){
        return ResponseEntity.ok(budgetService.getMyActiveBudgets());
    }

    @GetMapping("/history")
    public ResponseEntity<List<BudgetResponseDTO>> getBudgetHistory(){
        return ResponseEntity.ok(budgetService.getMyBudgetHistory());
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getAllBudgets(){
        return ResponseEntity.ok(budgetService.getPastAndActiveBudgets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> getBudgetById(@PathVariable Long budgetId)
    {
        return ResponseEntity.ok(budgetService.getBudgetById(budgetId));
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody @Valid BudgetRequestDTO dto){
        return ResponseEntity.ok(budgetService.createBudget(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @PathVariable Long budgetId,
            @RequestBody BudgetRequestDTO dto
    ){
        return ResponseEntity.ok(budgetService.updateBudget(budgetId,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId)
    {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
