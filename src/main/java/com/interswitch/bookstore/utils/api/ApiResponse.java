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


    private HttpStatus httpStatus;

    @JsonCreator
    public ApiResponse(T body) {

        super(new ApiResponseWrapper<>(body), HttpStatus.OK);
        httpStatus = HttpStatus.OK;
    }

    public ApiResponse(ApiResponseWrapper bodyWrapper){
        super(bodyWrapper, HttpStatus.OK);
        httpStatus = HttpStatus.OK;

    }

    public ApiResponse(ApiResponseWrapper bodyWrapper, HttpStatus status){
        super(bodyWrapper, status);
        httpStatus = status;

    }

    public ApiResponse(T body, Map<String, Object> meta) {

        super(new ApiResponseWrapper<>(body, meta), HttpStatus.OK);
        httpStatus = HttpStatus.OK;
    }

    public ApiResponse(Map<String, Object> validation) {
        super(new ApiResponseWrapper<>(validation), HttpStatus.BAD_REQUEST);
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public ApiResponse(String message, HttpStatus httpStatus){
        super(new ApiResponseWrapper<>(message, String.valueOf(httpStatus.value())), httpStatus);
        this.httpStatus = httpStatus;
    }

    public ApiResponse(String message, String statusCode, HttpStatus httpStatus){
        super(new ApiResponseWrapper<>(message, statusCode), httpStatus);
        this.httpStatus = httpStatus;
    }

    @JsonCreator
    public ApiResponse(@JsonProperty("body") T body, @JsonProperty("status") HttpStatus status) {

        super(new ApiResponseWrapper<>(body), status);
        httpStatus = status;
    }

    public ApiResponse(MultiValueMap<String, String> headers, HttpStatus status) {

        super(headers, status);
        httpStatus = status;
    }

    public ApiResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(new ApiResponseWrapper<>(body), headers, status);
        httpStatus = status;
    }

    public ApiResponse(String message, String status) {

        super(new ApiResponseWrapper<>(message, status), HttpStatus.BAD_REQUEST);
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }
}

