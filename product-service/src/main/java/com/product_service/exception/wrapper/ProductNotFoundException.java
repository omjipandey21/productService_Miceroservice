package com.product_service.exception.wrapper;

import java.io.Serial;
import java.io.Serializable;

public class ProductNotFoundException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1l;

    public ProductNotFoundException(){
    }

    public ProductNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    public ProductNotFoundException(String message){
        super(message);
    }

    public ProductNotFoundException(Long productId){
        super("Product not found with id: "+ productId);
    }
}
