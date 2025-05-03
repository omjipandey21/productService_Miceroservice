package com.product_service.entity.dto.mapper;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

//    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Product -> ProductDto
    ProductDto toDto(Product product);

    // ProductDto -> Product
    Product toEntity(ProductDto productDto);

}