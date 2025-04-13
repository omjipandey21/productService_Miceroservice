package com.product_service.entity.dto.mapper;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Product -> ProductDto
    @Mapping(source = "category", target = "category")
    ProductDto toDto(Product product);

    // ProductDto -> Product
    @Mapping(source = "entity.category", target = "category")
    @Mapping(target = "category")
    Product toEntity(ProductDto productDto);

}


//CategoryDto categoryDto = CategoryMapper.INSTANCE.toDto(category);
//Category categoryEntity = CategoryMapper.INSTANCE.toEntity(categoryDto);