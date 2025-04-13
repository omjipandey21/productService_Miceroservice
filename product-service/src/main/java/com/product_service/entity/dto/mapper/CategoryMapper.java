package com.product_service.entity.dto.mapper;


import com.product_service.entity.Category;
import com.product_service.entity.Product;
import com.product_service.entity.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {Product.class})
public interface CategoryMapper{

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Category -> CategoryDto
    @Mapping(source = "parentaCategory", target = "parentCategory")
    @Mapping(source = "subCategory", target = "subCategories")
    CategoryDto toDto(Category category);

    // CategoryDto -> Category
    @Mapping(source = "parentaCategory", target = "parentCategory")
    @Mapping(source = "subCategory", target = "subCategories")
    Category toEntity(CategoryDto categoryDto);
}


//CategoryDto categoryDto = CategoryMapper.INSTANCE.toDto(category);
//Category categoryEntity = CategoryMapper.INSTANCE.toEntity(categoryDto);
