package FinanceTracker.service;

import FinanceTracker.dto.CategoryRequestDTO;
import FinanceTracker.dto.CategoryResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.User;
import FinanceTracker.enums.TransactionType;
import FinanceTracker.mapper.CategoryMapper;
import FinanceTracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final RecurringPaymentsRepository recurringPaymentsRepository;

    //Get All categories for the user
    public List<CategoryResponseDTO> getAllCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllBaseAndUserCategories(userId);

        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    //Get categories by type of expenses
    public List<CategoryResponseDTO> getCategoriesByType(Long userId, TransactionType type) {
        List<Category> categories = categoryRepository.findAllBaseAndUserCategoriesByType(userId, type);

        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        boolean isSystem = category.getUser() == null;
        boolean isMine = category.getUser() != null && category.getUser().getId().equals(userId);

        if (!isSystem && !isMine) {
            throw new RuntimeException("Unauthorized! This category is not yours!");
        }

        return categoryMapper.toDto(category);
    }

    public List<CategoryResponseDTO> searchCategories(Long userId, String keyword) {
        return categoryRepository.searchByName(keyword, userId)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }


    //Merge Categories(Transfer all transaction from category A in category B and delete category A)
    public void mergeCategory(Long sourceCategoryId,Long targetCategoryId,Long userId) {

        Category sourceCategory=categoryRepository.findById(sourceCategoryId)
                .orElseThrow(() -> new RuntimeException("Source category not found"));

        Category targetCategory=categoryRepository.findById(targetCategoryId)
                .orElseThrow(() -> new RuntimeException("Source category not found"));

        if(sourceCategory.getUser()==null){
            throw new RuntimeException("Cannot delete/merge system category");
        }

        if (!sourceCategory.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized!Cannot delete/merge category that is not yours!");
        }

        boolean isTargetValid=targetCategory.getUser()==null || targetCategory.getUser().getId().equals(userId);

        if(!isTargetValid){
            throw new RuntimeException("Invalid target category");
        }

        transactionRepository.updateCategoryForTransactions(sourceCategoryId, targetCategoryId, userId);
        budgetRepository.updateCategoryForBudgets(sourceCategoryId, targetCategoryId, userId);
        recurringPaymentsRepository.updateCategoryForRecurringPayments(sourceCategoryId, targetCategoryId, userId);

        categoryRepository.delete(sourceCategory);
    }


    @Transactional
    public CategoryResponseDTO searchCategories(CategoryRequestDTO categoryRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (categoryRepository.existsByNameAndUserId(categoryRequestDTO.name(), userId)) {
            throw new RuntimeException("Category with these name already exists for you");
        }

        Category category = categoryMapper.toEntity(categoryRequestDTO);
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    public CategoryResponseDTO updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getUser() == null) {
            throw new RuntimeException("Cannot edit system categories");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized! You cannot update categories that is not yours!w");
        }

        if (categoryRepository.existsByNameAndUserIdExcludingCurrent(categoryRequestDTO.name(), userId, categoryId)) {
            throw new RuntimeException("Another category with this name already exists");
        }

        category.setName(categoryRequestDTO.name());
        category.setType(categoryRequestDTO.type());

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    //Delete our custom category
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getUser() == null) {
            throw new RuntimeException("Cannot delete system categories");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("You cannot delete this category.This category does not belong to you");
        }
        //Check if we have transactions with this category
        boolean isUsed = transactionRepository.existsByCategoryId(categoryId);

        if (isUsed) {
            throw new RuntimeException("Cannot delete category because it has related transactions. Please delete the transactions first.");
        }
        categoryRepository.delete(category);

    }

}
