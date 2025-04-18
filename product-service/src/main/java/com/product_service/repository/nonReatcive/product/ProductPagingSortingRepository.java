package com.product_service.repository.nonReatcive.product;

import com.product_service.entity.Product;
import org.hibernate.query.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;

public interface ProductPagingSortingRepository extends PagingAndSortingRepository<Product, Long> {

    Page findAll(Pageable pageable);
    Iterable findAll(Sort sort);
}
