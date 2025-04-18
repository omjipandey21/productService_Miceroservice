package com.product_service.exception;

import com.product_service.exception.wrapper.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest request;

    private ProblemDetail buildExceptionResponse(HttpStatus httpStatus, String errorMessage, String errorDetails, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle(errorMessage);
        problemDetail.setDetail(errorDetails);
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("errorCode", "PROD_404");
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        problemDetail.setProperty("timestamp", ZonedDateTime.now(ZoneId.systemDefault()));
        return problemDetail;
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException exception){
        log.info("Global Exception Handler, handling product not found");
        return buildExceptionResponse(HttpStatus.NOT_FOUND, "Product not found", exception.getMessage(), request);
    }

    //Handle Runtime Error
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleUnhandledException(RuntimeException exception){
        log.info("Global Exception handler, Unexpected error occurred");
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception){
        log.info("Global exception handler, Illegal argument exception");
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter", exception.getMessage(), request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception){
        log.info("Global exception handler, Null pointer exception");
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", "An unexpected error occurred.", request);
    }

    //Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentException(MethodArgumentNotValidException exception){
        log.info("Global exception handler, Method argument exception");
        String errorDetails = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildExceptionResponse(HttpStatus.BAD_REQUEST, "Validation failed", errorDetails, request);
    }

    //Handle Generic Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception){
        log.info("Global exception handler, Generic exception handler");
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Please contact support", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception){
        log.info("Global exception handler, Access denied exception");
        return buildExceptionResponse(HttpStatus.FORBIDDEN, "Access Denied", "You don't have permission to access this resource", request);
    }


}
