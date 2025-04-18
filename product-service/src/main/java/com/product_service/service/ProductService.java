package com.product_service.service;

import com.product_service.entity.dto.ProductDto;
import com.product_service.entity.enumm.Status;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProductService {

    // =============================================
    // BASIC CRUD OPERATIONS
    // =============================================
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long productId);
    List<ProductDto> findAllProducts();
    List<ProductDto> getProductsByIds(Set<Long> productIds);
    ProductDto updateProductById(Long productId, ProductDto productDto);
    void deleteProductById(Long productId);
    void deleteProductsByIds(Set<Long> productIds);
    boolean productExistsById(Long productId);

    // =============================================
    // FINDER METHODS
    // =============================================
    List<ProductDto> getProductsByName(String productName);
    List<ProductDto> getProductsByAttributeName(String attributeName);
    List<ProductDto> getProductsByAttributeNameContaining(String partialName);
    List<ProductDto> getProductsBySaleStatus(boolean onSale);
    List<ProductDto> getProductsByLowStockThreshold(Integer threshold);

    // =============================================
    // UPDATE OPERATIONS (SPECIFIC FIELDS)
    // =============================================
    ProductDto updateProductStatusById(Long productId, Status productStatus);
    ProductDto updateProductPrice(Long productId, BigDecimal updatedPrice);
    ProductDto updateProductSalePrice(Long productId, BigDecimal updatedSalePrice);
    ProductDto updateProductStockQuantity(Long productId, Integer newQuantity);
    ProductDto updateProductAttribute(Long productId, Map<String, String> newAttributeProperty);
    ProductDto updateProductDimensions(Long productId, Map<String, String> newDimensions);

    // =============================================
    // PAGINATION & SORTING
    // =============================================
    Page<ProductDto> getAllProductsPaginated(int pageNo, int pageSize, String sort);

    // =============================================
    // AGGREGATION & STATISTICS
    // =============================================
    Map<Status, List<ProductDto>> groupProductsByStatus();
    List<String> getAllProductNamesSorted();
    Long countActiveProducts();
    Optional<ProductDto> findMostExpensiveProduct();

    // =============================================
    // FILTERING METHODS
    // =============================================
    List<ProductDto> filterProductsByStatus(Status status);
    List<ProductDto> filterProductsByPriceRange(double minPrice, double maxPrice);

    // =============================================
    // REACTIVE API METHODS
    // =============================================
    Flux<ProductDto> getAllProductsReactive();
    Mono<ProductDto> getProductByIdReactive(Long productId);
}