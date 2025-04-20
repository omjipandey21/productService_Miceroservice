package com.product_service.exception.wrapper;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String errorMessage){
        super(errorMessage);
    }

    public CategoryNotFoundException(Long categoryId){
        super("Category not found with id: "+ categoryId);
    }
}
