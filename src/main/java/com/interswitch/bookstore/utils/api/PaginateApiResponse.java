package com.interswitch.bookstore.utils.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class PaginateApiResponse extends ResponseEntity<ApiResponseWrapper> {


    public PaginateApiResponse(PaginationBody paginationBody) {
        super(new ApiResponseWrapper<>(paginationBody), HttpStatus.OK);
    }
}
