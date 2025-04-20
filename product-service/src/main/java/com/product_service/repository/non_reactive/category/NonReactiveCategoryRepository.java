package com.product_service.repository.non_reactive.category;

import com.product_service.entity.Category;
import com.product_service.entity.enumm.Status;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NonReactiveCategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {


    @NonNull
    Page<Category> findAll(@NonNull Pageable pageable);

    Page<Category> findAll(Pageable pageable, Specification<Category> specification);

    @Query("Select c FROM Category c WHERE c.status = :status")
    List<Category> findByCategoryStatus(@Param("status") Status status);

    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName")
    List<Category> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.status = :status")
    List<Category> findByProductStatus(@Param("status") Status status);

    @Query("SELECT c FROM Category c JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findAllProductByCategoryId(@Param("id") Long id);

    @Query("SELECT c FROM Category c WHERE (SELECT p FROM Product p WHERE p.status = :status)")
    List<Category> findCategoryWithProductStatus(@Param("status") Status status);

    @Query("SELECT c.categoryName, COUNT(p) FROM Category c JOIN c.products p GROUP BY c.categoryName")
    Map<String, Long> getCategoryProductCounts();

//    @Query("""
//        SELECT new map(c.categoryName as name, COUNT(p) as productCount)
//        FROM Category c LEFT JOIN c.products p
//        GROUP BY c.categoryName
//        """)
//    Map<String, Long> getCategoryProductCounts();

}




