package com.product_service.entity.dto.mapper;


import com.product_service.entity.Category;
import com.product_service.entity.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CategoryMapper {


    @Mapping(target = "products", source = "products")
    @Mapping(target = "parentCategory", ignore = true) // Handle separately
    CategoryDto toDto(Category category);

    @Mapping(target = "products", ignore = true) // Will be set via service
    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    /**
     * Special mapping for parent category to prevent circular references
     */
    default CategoryDto mapParentCategory(Category category) {
        if (category == null || category.getParentCategory() == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getParentCategory().getId());
        dto.setCategoryName(category.getParentCategory().getCategoryName());
        dto.setSlug(category.getParentCategory().getSlug());
        // Don't map parent's parent to avoid infinite recursion
        return dto;
    }
}

