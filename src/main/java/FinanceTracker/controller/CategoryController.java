package FinanceTracker.controller;


import FinanceTracker.dto.CategoryRequestDTO;
import FinanceTracker.dto.CategoryResponseDTO;
import FinanceTracker.enums.TransactionType;
import FinanceTracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByType(@RequestParam TransactionType type) {
        return ResponseEntity.ok(categoryService.getCategoriesByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategories(@RequestParam String keyword) {
        return ResponseEntity.ok(categoryService.searchCategories(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequestDTO dto) {

        return ResponseEntity.ok(categoryService.updateCategory(id,dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCategories(
            @RequestParam Long sourceId,
            @RequestParam Long targetID) {
        categoryService.mergeCategory(sourceId, targetID);
        return ResponseEntity.ok().build();
    }

}
