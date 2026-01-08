package FinanceTracker.service;

import FinanceTracker.dto.CategoryRequestDTO;
import FinanceTracker.dto.CategoryResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.User;
import FinanceTracker.mapper.CategoryMapper;
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.UserRepository;
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

    //Get All categories for the user
    public List<CategoryResponseDTO> getAllCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllBaseAndUserCategories(userId);

        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    public void createCategory(CategoryRequestDTO categoryRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(categoryRepository.existsByNameAndUserId(categoryRequestDTO.name(), userId)) {
            throw new RuntimeException("Category with these name already exists for you");
        }

        Category category = categoryMapper.toEntity(categoryRequestDTO);
        category.setUser(user);

        categoryRepository.save(category);

    }


    //Delete our custom category
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if(category.getUser()==null)
        {
            throw new RuntimeException("Cannot delete system categories");
        }

        if(!category.getUser().getId().equals(userId)){
            throw new RuntimeException("You cannot delete this category.This category does not belong to you");
        }

        categoryRepository.delete(category);

    }

}
