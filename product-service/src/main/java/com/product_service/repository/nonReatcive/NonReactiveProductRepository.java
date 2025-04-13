package com.product_service.repository.nonReatcive;

import com.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NonReactiveProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductName(String productName);

    @Query("Select p from Product p JOIN p.attributes a WHERE KEY(a) = :attributeName AND a = :attributeValue")
    List<Product> findByAttributesKeyAndValue(@Param("attributeName") String attributeName,
                                   @Param("attributeValue") String attributeValue);

    @Query("SELECT p FROM Product p JOIN p.attributes a WHERE KEY(a) = :attributeName")
    List<Product> findByAttributesKey(@Param("attributeName") String attributeName);

    @Query(value = "SELECT DISTINCT p.* FROM Product p " +
            "JOIN product_attributes pa ON p.id = pa.product_id " +
            "WHERE pa.attribute_value ILIKE %:partialValue%", nativeQuery = true)
    List<Product> findByPartialAttributeValue(@Param("partialValue") String partialValues);

    List<Product> findByIsOnSale(boolean value);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByProductsRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    @Query("SELECT p FROM Product p WHERE p.lowStockThreshold :threshold")
    List<Product> findByLowStockThreshold(@Param("threshold") int threshold);

    // update queries ------


}
