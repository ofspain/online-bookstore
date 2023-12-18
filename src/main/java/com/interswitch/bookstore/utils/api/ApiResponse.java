package com.interswitch.bookstore.utils.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.Map;


public class ApiResponse<T> extends ResponseEntity<ApiResponseWrapper<T>> implements Serializable {

    @JsonCreator
    public ApiResponse(T body) {

        super(new ApiResponseWrapper<>(body), HttpStatus.OK);
    }

    public ApiResponse(T body, Map<String, Object> meta) {

        super(new ApiResponseWrapper<>(body, meta), HttpStatus.OK);
    }

    public ApiResponse(Map<String, Object> validation) {
        super(new ApiResponseWrapper<>(validation), HttpStatus.BAD_REQUEST);
    }

    public ApiResponse(String message, HttpStatus httpStatus){
        super(new ApiResponseWrapper<>(message, String.valueOf(httpStatus.value())), httpStatus);
    }

    public ApiResponse(String message, String statusCode, HttpStatus httpStatus){
        super(new ApiResponseWrapper<>(message, statusCode), httpStatus);
    }

    @JsonCreator
    public ApiResponse(@JsonProperty("body") T body, @JsonProperty("status") HttpStatus status) {

        super(new ApiResponseWrapper<>(body), status);
    }

    public ApiResponse(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    public ApiResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(new ApiResponseWrapper<>(body), headers, status);
    }

    public ApiResponse(String message, String status) {

        super(new ApiResponseWrapper<>(message, status), HttpStatus.BAD_REQUEST);
    }
}

