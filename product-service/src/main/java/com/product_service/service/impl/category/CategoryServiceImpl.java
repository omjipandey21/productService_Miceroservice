package com.product_service.service.impl.category;

import com.product_service.constant.AppConstant;
import com.product_service.entity.Category;
import com.product_service.entity.dto.CategoryDto;
import com.product_service.entity.dto.mapper.CategoryMapper;
import com.product_service.entity.enumm.Status;
import com.product_service.exception.wrapper.CategoryNotFoundException;
import com.product_service.repository.non_reactive.category.NonReactiveCategoryRepository;
import com.product_service.service.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final EntityManager entityManager;
    private final NonReactiveCategoryRepository nonReactiveCategoryRepository;

    private Status parseStatus(String statusStr) {
        try {
            return Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + statusStr +
                            ". Allowed: ACTIVE, INACTIVE, DISCONTINUED, OUT_OF_STOCK"
            );
        }
    }

    // ==================== CRUD Operations ====================
    @Transactional
    @Override
    public CategoryDto createCategory(@NonNull CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto.getCategoryName());
        Category savedCategory = nonReactiveCategoryRepository.save(CategoryMapper.INSTANCE.toEntity(categoryDto));
        return CategoryMapper.INSTANCE.toDto(savedCategory);
    }

    @Transactional
    @Override
    public List<CategoryDto> bulkCreateCategories(List<CategoryDto> categoryDto) {
        if (categoryDto == null || categoryDto.isEmpty()) {
            throw new IllegalArgumentException("Category list cannot be null or empty");
        }
        log.info("Creating {} categories", categoryDto.size());
        return nonReactiveCategoryRepository.saveAll(
                categoryDto.stream()
                        .map(CategoryMapper.INSTANCE::toEntity)
                        .toList()
                )
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    @Cacheable(value = "category", key = "#categoryId", unless = "#result.status == T(com.product_service.entity.enumm.Status).INACTIVE")
    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return nonReactiveCategoryRepository.findById(categoryId)
                .map(CategoryMapper.INSTANCE::toDto)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Transactional
    @CachePut(value = "category", key = "#categoryId")
    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        nonReactiveCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        categoryDto.setId(categoryId);
        return CategoryMapper.INSTANCE.toDto(nonReactiveCategoryRepository.save(CategoryMapper.INSTANCE.toEntity(categoryDto)));
    }

    // ==================== Status Management ====================
    @CacheEvict(value = "category", key = "#categoryId")
    @Override
    public CategoryDto changeCategoryStatusById(Long categoryId, String status){
        Status parsedStatus = parseStatus(status);
        Category category = nonReactiveCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.setStatus(parsedStatus);
        return CategoryMapper.INSTANCE.toDto(nonReactiveCategoryRepository.save(category));
    }

    @Transactional
    @Override
    @CacheEvict(value = "category", allEntries = true)
    public void bulkUpdateStatus(Set<Long> categoryIds, Status status) {
        List<Category> categories = nonReactiveCategoryRepository.findAllById(categoryIds);
        categories.forEach(category -> category.setStatus(status));
        nonReactiveCategoryRepository.saveAll(categories);
        log.info("Updated status to {} for {} categories", status, categories.size());
    }

    // ==================== Bulk Operations ====================
    @CacheEvict(value = "category", key = "#categoryId")
    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        if (!nonReactiveCategoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        nonReactiveCategoryRepository.deleteById(categoryId);
        log.info("Deleted category with id: {}", categoryId);
    }

    @Transactional
    @CacheEvict(value = "category", allEntries = true)
    @Override
    public void bulkDeleteCategories(Set<Long> categoryIds) {
        nonReactiveCategoryRepository.deleteAllById(categoryIds);
        log.info("Deleted {} categories", categoryIds.size());
    }

    // ==================== Query Methods ====================
    @Override
    @Transactional(readOnly = true)
    public boolean categoryExistsById(Long categoryId) {
        return nonReactiveCategoryRepository.existsById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllCategories() {
        return nonReactiveCategoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return nonReactiveCategoryRepository.findAll()
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getCategoriesWithPagination(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return nonReactiveCategoryRepository.findAll(pageable)
                .map(CategoryMapper.INSTANCE::toDto);
    }


    // ==================== Status-based Queries ====================
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByStatus(String status) {
        Status parsedStatus = parseStatus(status);
        return nonReactiveCategoryRepository.findByCategoryStatus(parsedStatus)
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    // ==================== Search & Filtering ====================
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> searchCategoriesByName(String name) {
        return nonReactiveCategoryRepository.findByCategoryName(name)
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> filterCategories(Map<String, String> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> query = cb.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {

                switch (key) {
                    case AppConstant.CATEGORY_NAME:
                        predicates.add(cb.like(root.get(AppConstant.CATEGORY_NAME), "%" + value + "%"));
                        break;
                    case AppConstant.SLUG:
                        predicates.add(cb.equal(root.get(AppConstant.SLUG), value));
                        break;
                    case AppConstant.ACTIVE:
                        predicates.add(cb.equal(root.get(AppConstant.ACTIVE), Boolean.parseBoolean(value)));
                        break;
                    default:
                        break;
                }
            }
        });

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Category> result = entityManager.createQuery(query).getResultList();
        return result.stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> filterCategoriesBySpecification(Map<String, String> parameters) {
        Specification<Category> categorySpecification = Specification.where(null);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null && !value.isEmpty()) {
                categorySpecification = categorySpecification.and((root, query, criteriaBuilder) ->
                        switch (key) {
                            case AppConstant.CATEGORY_NAME -> criteriaBuilder.like(root.get(AppConstant.CATEGORY_NAME), "%" + value + "%");
                            case AppConstant.SLUG -> criteriaBuilder.equal(root.get(AppConstant.SLUG), value);
                            case AppConstant.ACTIVE -> criteriaBuilder.equal(root.get(AppConstant.ACTIVE), Boolean.parseBoolean(value));
                            default -> null;
                        }
                );
            }
        }
        return nonReactiveCategoryRepository.findAll(categorySpecification)
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }


    // ==================== Advanced Operations ====================
    @Override
    public List<CategoryDto> getCategoriesWithProducts(Long categoryId) {
        return nonReactiveCategoryRepository.findAllProductByCategoryId(categoryId)
                .stream().map(CategoryMapper.INSTANCE::toDto).toList();
    }

    @Override
    public List<CategoryDto> getCategoriesWithProductsStatus(String status) {
        Status parsedStatus = parseStatus(status);
        return nonReactiveCategoryRepository.findCategoryWithProductStatus(parsedStatus)
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public Map<String, Long> getCategoryProductCounts() {
        return nonReactiveCategoryRepository.getCategoryProductCounts();
    }

}


