package com.product_service.entity;

import com.product_service.entity.enumm.Status;
import com.product_service.entity.mappedEntity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(name = "uc_product_slug", columnNames = "slug"),
        @UniqueConstraint(name = "uc_product_sku", columnNames = "sku")
})
public class Product extends AuditableEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "is_on_sale")
    private Boolean isOnSale;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    // Many products belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // For attributes like "Color: Red", "Size: M"
    @ElementCollection
    @CollectionTable(name = "product_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "weight_unit")
    private String weightUnit;

    @ElementCollection
    @CollectionTable(name = "product_dimensions", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "dimension_type") // e.g., height, width, depth
    @Column(name = "dimension_value")
    private Map<String, String> dimension;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
