package com.interswitch.bookstore.exceptions;

public enum Messages {


    MISSING_AUTH_TOKEN("2001", "Authentication token not found"),

    INVALID_AUTH_TOKEN("2002", "Authentication token is invalid"),

    UNKNOW_AUTH_ERROR("2003", "Unidentified authentication error"),

    NO_AUTH_USER("2004", "No authenticated user"),

    NO_USER("2005", "No such user"),

    WRONG_PASSWORD("2006", "Wrong password"),



    PAYMENT_GATEWAY_NO_ACTIVE("3000", "No payment gateway supported at the moment"),
    PAYMENT_INVALID_PAYMENT_METHOD("3002", "Payment method not supported"),

    INCONSISTENT_CART_STATE("3001", "Inconsistent cart state trigger");



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
