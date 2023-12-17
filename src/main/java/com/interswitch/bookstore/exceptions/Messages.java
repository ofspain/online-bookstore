package com.interswitch.bookstore.exceptions;

public enum Messages {


    MISSING_AUTH_TOKEN("2001", "Authentication token not found"),

    INVALID_AUTH_TOKEN("2002", "Authentication token is invalid"),

    UNKNOW_AUTH_ERROR("2003", "Unidentified authentication error");


    private final String code;
    private final String message;

    public String getCode(){
        return code;
    }


    public String getMessage(){
        return message;
    }
    Messages(String code, String message){
        this.code = code;
        this.message = message;
    }
}
