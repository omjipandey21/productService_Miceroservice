package com.product_service.repository.reactive;

import com.product_service.entity.Product;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.query.Page;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.awt.print.Pageable;

public interface ReactiveProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findByCategroyId(Long categoryId);
    Flux<Product> findByProductName(String productName);
    Flux<Product> findByIsOnSale(Boolean result);
    Flux<Product> findByIsAvailable(Boolean result);
    Flux<Product> findAllByPagingAndSorting(Pageable pageable);
    Flux<Product> findAllWithPagination(Page page, Size size);
    Flux<Product> findByBrandName(String brandName);
}
