package com.product_service.service.impl;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductDto;
import com.product_service.entity.dto.mapper.ProductMapper;
import com.product_service.entity.enumm.Status;
import com.product_service.exception.ProductNotFoundException;
import com.product_service.repository.nonReatcive.NonReactiveProductRepository;
import com.product_service.repository.nonReatcive.ProductPagingSortingRepository;
import com.product_service.repository.reactive.ReactiveProductRepository;
import com.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ReactiveProductRepository reactiveProductRepository;
    private final ProductPagingSortingRepository productPagingSortingRepository;
    private final NonReactiveProductRepository nonReactiveProductRepository;
    private final CacheManager cacheManager;

    // save product
    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.INSTANCE.toEntity(productDto);
        Product createdProduct = nonReactiveProductRepository.save(product);
        return ProductMapper.INSTANCE.toDto(createdProduct);
    }

    // delete product
    @Transactional
    @CacheEvict(value = "product", key ="#productId")
    @Override
    public void deleteProductById(Long productId) throws RuntimeException {
        // implement cache here to fetch the product
        // 1
        if (!nonReactiveProductRepository.existsById(productId)){
            throw new ProductNotFoundException(productId);
        }
        nonReactiveProductRepository.deleteById(productId);

        // 2
//        Optional.of(nonReactiveProductRepository.findById(productId)).ifPresentOrElse(
//                product -> nonReactiveProductRepository.deleteById(productId),
//                () -> {
//                    throw new RuntimeException("Product not found with id: "+ productId);
//                }
//        );
    }

    // get product related APIS implementation

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
    public boolean productExistById(Long productId){
        return nonReactiveProductRepository.existsById(productId);
    }

    @Override
    public List<ProductDto> getProductByName(String productName){
        List<Product> matchingProduct = nonReactiveProductRepository.findByProductName(productName);
        if (matchingProduct.isEmpty()){
            throw new RuntimeException("Products not found with name: "+ productName);
        }
        return matchingProduct.stream()
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductByAttributes(String attributeName){
        return nonReactiveProductRepository.findByPartialAttributeValue(attributeName)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductByPartialAttributeNames(String searchValue){
        return nonReactiveProductRepository.findByPartialAttributeValue(searchValue)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductByOnSale(boolean productOnSale){
        return nonReactiveProductRepository.findByIsOnSale(productOnSale)
                .stream().map(ProductMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductByPriceInBetween(Double minPrice, Double maxPrice){
        return nonReactiveProductRepository.findByProductsRange(minPrice, maxPrice)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductByOnLowStocks(Integer threshold){
        return nonReactiveProductRepository.findByLowStockThreshold(threshold)
                .stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    // update product related APIs implementation

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
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductStatusById(Long productId, Status status){
        Product product = nonReactiveProductRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));
        product.setStatus(status);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductPrice(Long productId, Double updatedPrice){

        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setPrice(updatedPrice);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

    @Transactional
    @CachePut(value = "product", key = "#productId")
    @Override
    public ProductDto updateProductSalePrice(Long productId, Double updateSalePrice){
        Product product = nonReactiveProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setSalePrice(updateSalePrice);
        Product updatedProduct = nonReactiveProductRepository.save(product);
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
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
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
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
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
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
        Cache cache = cacheManager.getCache("product");
        if(cache != null){
            cache.put(productId, updatedProduct);
        }
        return ProductMapper.INSTANCE.toDto(updatedProduct);
    }

}
