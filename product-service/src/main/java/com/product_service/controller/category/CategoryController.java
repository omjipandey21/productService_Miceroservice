package com.product_service.controller.category;

import com.product_service.controller.dto.request.BulkCategoryStatusUpdateRequest;
import com.product_service.entity.dto.CategoryDto;
import com.product_service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "Operations for creating, updating, querying and deleting product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a single new category and returns the created object.")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto created = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create categories in bulk", description = "Creates multiple categories at once.")
    @ApiResponse(responseCode = "201", description = "Categories created successfully")
    public ResponseEntity<List<CategoryDto>> createCategoriesBatch(
            @RequestBody @Valid List<@Valid CategoryDto> categoryDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.bulkCreateCategories(categoryDtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Fetches a category based on the provided ID.")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category by ID", description = "Updates a category using the provided ID and request body.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change category status", description = "Updates the status of a specific category by ID.")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    public ResponseEntity<CategoryDto> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(categoryService.changeCategoryStatusById(id, status));
    }

    @PatchMapping("/status/batch")
    @Operation(summary = "Bulk status update", description = "Updates the status of multiple categories at once.")
    @ApiResponse(responseCode = "200", description = "Statuses updated successfully")
    public ResponseEntity<Void> bulkUpdateCategoryStatus(@RequestBody @Valid BulkCategoryStatusUpdateRequest request) {
        categoryService.bulkUpdateStatus(request.getCategoryIds(), request.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID", description = "Deletes a single category by its ID.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "Bulk delete categories", description = "Deletes multiple categories by their IDs.")
    @ApiResponse(responseCode = "204", description = "Categories deleted successfully")
    public ResponseEntity<Void> deleteCategoriesBatch(@RequestBody Set<Long> ids) {
        categoryService.bulkDeleteCategories(ids);
        return ResponseEntity.noContent().build();
    }

    // ========= Query Endpoints =========

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if category exists", description = "Returns true if the category with the given ID exists.")
    public ResponseEntity<Boolean> checkCategoryExists(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.categoryExistsById(id));
    }

    @GetMapping("/count")
    @Operation(summary = "Count all categories", description = "Returns the total number of categories.")
    public ResponseEntity<Long> getCategoryCount() {
        return ResponseEntity.ok(categoryService.countAllCategories());
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns a list of all available categories.")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get paginated categories", description = "Returns a page of categories based on pagination and sorting parameters.")
    public ResponseEntity<Page<CategoryDto>> getPaginatedCategories(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @RequestParam String direction) {
        Page<CategoryDto> paged = categoryService.getCategoriesWithPagination(page, size, sort, direction);
        return ResponseEntity.ok(paged);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get categories by status", description = "Returns a list of categories with the given status.")
    public ResponseEntity<List<CategoryDto>> getCategoriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(categoryService.getCategoriesByStatus(status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories by name", description = "Performs a name-based search for categories.")
    public ResponseEntity<List<CategoryDto>> searchCategories(@RequestParam String name) {
        return ResponseEntity.ok(categoryService.searchCategoriesByName(name));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter categories", description = "Filters categories using various query parameters.")
    public ResponseEntity<List<CategoryDto>> filterCategories(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(categoryService.filterCategories(filters));
    }

    @PostMapping("/search/advanced")
    @Operation(summary = "Advanced category search", description = "Search categories using complex criteria passed in the request body.")
    public ResponseEntity<List<CategoryDto>> advancedCategorySearch(@RequestBody Map<String, String> criteria) {
        return ResponseEntity.ok(categoryService.filterCategoriesBySpecification(criteria));
    }

    // ========= Product-Based Endpoints =========

    @GetMapping("/{id}/with-products")
    @Operation(summary = "Get category with products", description = "Returns the specified category and its related products.")
    public ResponseEntity<List<CategoryDto>> getCategoryWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoriesWithProducts(id));
    }

    @GetMapping("/by-product-status/{status}")
    @Operation(summary = "Get categories by product status", description = "Returns categories whose products match the given status.")
    public ResponseEntity<List<CategoryDto>> getCategoriesByProductStatus(@PathVariable String status) {
        return ResponseEntity.ok(categoryService.getCategoriesWithProductsStatus(status));
    }
}
