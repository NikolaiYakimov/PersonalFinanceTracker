package FinanceTracker.mapper;

import FinanceTracker.dto.CategoryRequestDTO;
import FinanceTracker.dto.CategoryResponseDTO;
import FinanceTracker.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toDto(Category category){
        if(category== null){
            throw new IllegalArgumentException("Category cannot be null");

        }

        boolean isCustom=(category.getUser()!=null);

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getType(),
                isCustom
        );
    }


    public Category toEntity(CategoryRequestDTO dto){
        if(dto==null){
            throw new IllegalArgumentException("CategoryRequestDTO cannot be null");

        }

        Category category=new Category();
        category.setName(dto.name());
        category.setType(dto.type());

        return category;
    }
}
