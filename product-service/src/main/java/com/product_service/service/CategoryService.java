package com.product_service.service;

import com.product_service.entity.dto.CategoryDto;
import com.product_service.entity.enumm.Status;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CategoryService {

    // ==================== CRUD Operations ====================
    CategoryDto createCategory(CategoryDto categoryDto);
    List<CategoryDto> bulkCreateCategories(List<CategoryDto> categoryDto);

    CategoryDto getCategoryById(Long categoryId);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    // ==================== Status Management ====================
    CategoryDto changeCategoryStatusById(Long categoryId, String status);
    void bulkUpdateStatus(Set<Long> categoryIds, Status status);

    // ==================== Bulk Operations ====================
    void deleteCategoryById(Long categoryId);
    void bulkDeleteCategories(Set<Long> categoryIds);

    // ==================== Query Methods ====================
    boolean categoryExistsById(Long categoryId);
    Long countAllCategories();
    List<CategoryDto> getAllCategories();
    Page<CategoryDto> getCategoriesWithPagination(int page, int size, String sortBy, String direction);

    // ==================== Status-based Queries ====================
    List<CategoryDto> getCategoriesByStatus(String status);

    // ==================== Search & Filtering ====================
    List<CategoryDto> searchCategoriesByName(String name);
    List<CategoryDto> filterCategories(Map<String, String> filters);
    List<CategoryDto> filterCategoriesBySpecification(Map<String, String> filters);

    // ==================== Advanced Operations ====================
    List<CategoryDto> getCategoriesWithProducts(Long categoryId);
    List<CategoryDto> getCategoriesWithProductsStatus(String status);
    Map<String, Long> getCategoryProductCounts();

}
