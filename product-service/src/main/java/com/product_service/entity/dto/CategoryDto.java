package com.product_service.entity.dto;

import com.product_service.entity.enumm.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name cannot exceed 255 characters")
    private String categoryName;

    @NotBlank(message = "Slug is required")
    private String slug;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Status is required")
    private Status status;
    private String imageUrl;
    private boolean isDeleted;
    private CategoryDto parentCategory;
    private List<CategoryDto> subCategories;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ProductDto> product;

}

