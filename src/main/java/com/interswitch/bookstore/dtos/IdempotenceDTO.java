package com.interswitch.bookstore.dtos;


import com.interswitch.bookstore.utils.api.ApiResponseWrapper;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class IdempotenceDTO<T> {

    private ApiResponseWrapper<T> responseWrapper;
    private HttpStatus status;

    public IdempotenceDTO(){}

    public IdempotenceDTO(ApiResponseWrapper<T> responseWrapper, HttpStatus status){
        this.responseWrapper = responseWrapper;
        this.status = status;
    }

}
