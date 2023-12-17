package com.interswitch.bookstore.exceptions;

import com.interswitch.bookstore.utils.api.ApiResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handle(MethodArgumentNotValidException e){

        return getObjectApiResponse(e);
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handle(BindException e){

        return getObjectApiResponse(e);
    }

    private ApiResponse<Object> getObjectApiResponse(BindException e) {
        Map<String, Object> errors = new HashMap<>();
        for(FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            errors.put(fieldName, fieldError.getDefaultMessage());
        }
        return new ApiResponse<>(errors);
    }



    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ApiResponse<Object> handle(HttpMediaTypeNotAcceptableException e){

        return new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientAbortException.class)
    public ApiResponse<Object> handle(ClientAbortException e){

        return new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Object> handle(NotFoundException e){

        return new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handle(Exception e){

        return new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NonTransientDataAccessException.class)
    public ApiResponse<Object> handle(NonTransientDataAccessException e){
        e.printStackTrace();
        return new ApiResponse<>(e.getMessage(), "400", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<Object> handle(DataIntegrityViolationException e){
        e.printStackTrace();
        return new ApiResponse<>(e.getMessage(), "400", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handle(RuntimeException e){
        e.printStackTrace();
        return new ApiResponse<>(e.getMessage() == null ? e.getCause().getMessage() : e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
