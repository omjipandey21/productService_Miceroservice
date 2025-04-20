package com.product_service.entity.dto;

import com.product_service.entity.enumm.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    private String categoryName;
    private String slug;
    private String description;
    private Status status;
    private String imageUrl;
    private boolean isDeleted;
    private CategoryDto parentCategory;
    private List<CategoryDto> subCategories;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ProductDto> product;

}

