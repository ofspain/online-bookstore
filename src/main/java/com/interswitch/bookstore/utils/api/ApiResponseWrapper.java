package com.interswitch.bookstore.utils.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ApiResponseWrapper<T> {
    private String status = "00";

    private String message = "Ok";

    // @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private T data;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JsonProperty("meta")
    private Map<String, Object> meta;

    @JsonIgnore
    private HttpStatus httpStatus = HttpStatus.OK;

    public ApiResponseWrapper(){}


    public ApiResponseWrapper(T body) {
        setData(body);
    }

    public ApiResponseWrapper(T body, Map<String, Object> meta) {
        setData(body);
        setMeta(meta);
    }

    public ApiResponseWrapper(Map<String, Object> meta) {
        setStatus("400");
        setMessage("Validation Error");
        setMeta(meta);
    }

    /**to use for excpetions**/
    public ApiResponseWrapper(String message, String statusCode){
        setStatus(statusCode);
        setMessage(message);
    }
}
