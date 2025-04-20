package com.product_service.entity.dto;

import com.product_service.entity.enumm.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long productId;
    private String productName;
    private String slug;
    private String description;
    private String sku;
    private String brand;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String currency;
    private Boolean isOnSale;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean isAvailable;
    private CategoryDto category;
    private Map<String, String> attributes;
    private String imageURL;
    private String thumbnailUrl;
    private Double weight;
    private String weightUnit;
    private Map<String, String> dimension;
    private Instant createdAt;
    private Instant updatedAt;
    private Status status;

}
