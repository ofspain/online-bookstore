package com.interswitch.bookstore.exceptions;

abstract class ApiException extends RuntimeException{

    private String code;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Messages messageCode){
        super(messageCode.getMessage());
        this.code = messageCode.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

