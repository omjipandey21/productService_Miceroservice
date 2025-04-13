package com.product_service.service;

import com.product_service.entity.dto.ProductDto;
import com.product_service.entity.enumm.Status;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ProductService {

    // save product
    ProductDto createProduct(ProductDto productDto);

    // delete product
    void deleteProductById(Long productId);

    // get product related methods
    boolean productExistById(Long productId);
    ProductDto getProductById(Long productId);
    List<ProductDto> findAllProducts();
    List<ProductDto> getProductByName(String productName);
    List<ProductDto> getProductByAttributes(String attributeName);
    List<ProductDto> getProductByPartialAttributeNames (String partialName);
    List<ProductDto> getProductByOnSale(boolean productOnSale);


    List<ProductDto> getProductByPriceInBetween(Double minPrice, Double maxPrice);
    List<ProductDto> getProductByOnLowStocks(Integer threshold);

    // update products related methods
    ProductDto updateProductById(Long productId, ProductDto productDto);
    ProductDto updateProductStatusById(Long productId, Status productStatus);
    ProductDto updateProductPrice(Long productId, Double updatedPrice);
    ProductDto updateProductSalePrice(Long productId, Double updateSalePrice);
    ProductDto updateProductStockQuantity(Long productId, Integer newQuantity);
    ProductDto updateProductAttribute(Long productId, Map<String, String> newAttributeProperty);
    ProductDto updateProductDimensions(Long productID, Map<String , String> newDimensions);
}
