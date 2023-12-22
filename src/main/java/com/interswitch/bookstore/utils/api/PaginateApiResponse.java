package com.interswitch.bookstore.utils.api;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class PaginateApiResponse<T> extends ResponseEntity<ApiResponseWrapper> {

    private PaginationBody<T> paginationBody;

    public PaginateApiResponse(){
        super(new ApiResponseWrapper<>(new PaginationBody()),HttpStatus.OK);
    }

    public PaginateApiResponse(PaginationBody<T> paginationBody) {
        super(new ApiResponseWrapper<>(paginationBody), HttpStatus.OK);
        this.paginationBody = paginationBody;
    }

    public PaginationBody<T> getPaginationBody(){
        return paginationBody;
    }
}
