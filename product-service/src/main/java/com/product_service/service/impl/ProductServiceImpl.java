package com.product_service.service.impl;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductDto;
import com.product_service.entity.dto.mapper.ProductMapper;
import com.product_service.entity.enumm.Status;
import com.product_service.exception.wrapper.ProductNotFoundException;
import com.product_service.repository.nonReatcive.product.NonReactiveProductRepository;
import com.product_service.repository.reactive.ReactiveProductRepository;
import com.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ReactiveProductRepository reactiveProductRepository;
    private final NonReactiveProductRepository nonReactiveProductRepository;
    private final CacheManager cacheManager;

    // =============================================
    // BASIC CRUD OPERATIONS
    // =============================================

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.INSTANCE.toEntity(productDto);
        Product createdProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(createdProduct);
    }

    @Override
    @Cacheable(value = "product", key = "#productId")
    public ProductDto getProductById(Long productId){
        return nonReactiveProductRepository.findById(productId)
                .map(ProductMapper.INSTANCE::toDto)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public List<ProductDto> findAllProducts(){
        return nonReactiveProductRepository.findAll()
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductsByIds(Set<Long> productIds) {
        List<ProductDto> foundProductList = new ArrayList<>();
        productIds.forEach(productId -> {
            Product productFound = nonReactiveProductRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));
            foundProductList.add(ProductMapper.INSTANCE.toDto(productFound));
        });
        if (foundProductList.isEmpty()){
            log.warn("Unknown exception occurred while fetching the product list");
            throw new RuntimeException("Unknown exception occurred");
        }
        log.info("Product list found");
        return foundProductList;
    }

    @Transactional  // the use of transactional ensures for atomicity : all success or roll back
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductById(Long productId, ProductDto productDto){
        Cache cache = cacheManager.getCache("product");
        Product cachedProduct = (cache != null) ? cache.get(productId, Product.class) : null;

        log.info(cachedProduct != null ? "Product found in cache" : "Product not found in cache");

        Product updatingProduct = ProductMapper.INSTANCE.toEntity(productDto);
        updatingProduct.setProductId(productId);

        if(cachedProduct == null){
            nonReactiveProductRepository.findById(productId).orElseThrow(() ->
                    new ProductNotFoundException(productId));
        }

        Product updatedProduct = nonReactiveProductRepository.save(updatingProduct);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = "product", key ="#productId")
    @Override
    public void deleteProductById(Long productId) throws RuntimeException {
        if (!nonReactiveProductRepository.existsById(productId)){
            throw new ProductNotFoundException(productId);
        }
        nonReactiveProductRepository.deleteById(productId);
        log.info("Product deleted with id: {}", productId);
    }

    @Transactional
    @CacheEvict(value = "product", allEntries = true)
    @Override
    public void deleteProductsByIds(Set<Long> productIds) {
        log.info("Deleting products by Ids: {}", productIds);
        nonReactiveProductRepository.deleteAllById(productIds);
        log.info("Products deleted having ids: {}", productIds);
    }

    @Override
    public boolean productExistsById(Long productId){
        return nonReactiveProductRepository.existsById(productId);
    }

    // =============================================
    // FINDER METHODS
    // =============================================

    @Override
    public List<ProductDto> getProductsByName(String productName){
        List<Product> matchingProduct = nonReactiveProductRepository.findByProductName(productName);
        if (matchingProduct.isEmpty()){
            throw new ProductNotFoundException("Products not found with name: "+ productName);
        }
        return matchingProduct.stream()
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductsByAttributeName(String attributeName){
        return nonReactiveProductRepository.findByPartialAttributeValue(attributeName)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductsByAttributeNameContaining(String searchValue){
        return nonReactiveProductRepository.findByPartialAttributeValue(searchValue)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsBySaleStatus(boolean productOnSale){
        return nonReactiveProductRepository.findByIsOnSale(productOnSale)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByLowStockThreshold(Integer threshold){
        return nonReactiveProductRepository.findByLowStockThreshold(threshold)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    // =============================================
    // UPDATE OPERATIONS (SPECIFIC FIELDS)
    // =============================================

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductStatusById(Long productId, Status status){
        Product product = nonReactiveProductRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));
        product.setStatus(status);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductPrice(Long productId, BigDecimal updatedPrice){

        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setPrice(updatedPrice);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductSalePrice(Long productId, BigDecimal updateSalePrice){
        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setSalePrice(updateSalePrice);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductStockQuantity(Long productId, Integer stockQty){
        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setStockQuantity(stockQty);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductAttribute(Long productId, Map<String, String> newAttributes){
        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setAttributes(newAttributes);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @CachePut(value = "product", key = "#productId")
    @Transactional
    @Override
    public ProductDto updateProductDimensions(Long productId, Map<String, String> newDimensions){
        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setDimension(newDimensions);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    // =============================================
    // PAGINATION & SORTING
    // =============================================

    @Override
    public Page<ProductDto> getAllProductsPaginated(int pageNo, int pageSize, String sortByPropertyName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc(sortByPropertyName)));
        return nonReactiveProductRepository.findAll(pageable)
                .map(ProductMapper.INSTANCE::toDto);
    }

    // =============================================
    // AGGREGATION & STATISTICS
    // =============================================

    @Override
    public Map<Status, List<ProductDto>> groupProductsByStatus() {
        return nonReactiveProductRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getStatus,
                        Collectors.mapping(ProductMapper.INSTANCE::toDto, Collectors.toList())));
    }

    @Override
    public List<String> getAllProductNamesSorted() {
        return nonReactiveProductRepository.findAll()
                .stream()
                .map(Product::getProductName)
                .sorted()
                .toList();
    }

    @Override
    public Long countActiveProducts() {
        return nonReactiveProductRepository.findAll()
                .stream()
                .filter(Product::getIsAvailable)
                .count();
    }

    @Override
    public Optional<ProductDto> findMostExpensiveProduct() {
        return Optional.of(nonReactiveProductRepository.findByMostExpensiveProduct()
                        .map(ProductMapper.INSTANCE::toDto))
                .orElseThrow(() -> new ProductNotFoundException("No product could be found"));
    }

    // =============================================
    // FILTERING METHODS
    // =============================================

    @Override
    public List<ProductDto> filterProductsByStatus(Status status) {
        return nonReactiveProductRepository.findAll()
                .stream()
                .filter(product -> product.getStatus().equals(status))
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> filterProductsByPriceRange(double minPrice, double maxPrice){
        return nonReactiveProductRepository.findByProductsRange(minPrice, maxPrice)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    // =============================================
    // REACTIVE API METHODS
    // =============================================

    @Override
    public Flux<ProductDto> getAllProductsReactive() {
        return reactiveProductRepository.findAll()
                .switchIfEmpty(Flux.error(() -> new ProductNotFoundException("Products could not be found")))
                .map(ProductMapper.INSTANCE::toDto)
                .doOnComplete(() -> log.info("All products are fetched"))
                .doOnError(error -> log.warn("Error fetching the list of product: {}", error.getMessage()));
    }

    @Override
    public Mono<ProductDto> getProductByIdReactive(Long productId) {
        return reactiveProductRepository.findById(productId)
                .switchIfEmpty(Mono.error(() -> new ProductNotFoundException(productId)))
                .map(ProductMapper.INSTANCE::toDto)
                .doOnSuccess(success -> log.info("Product found"))
                .doOnError(error -> log.warn("Error occurred while fetching product with id: {}", error.getMessage()));
    }

}
