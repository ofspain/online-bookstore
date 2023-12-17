package com.interswitch.bookstore.exceptions;


public class AuthenticationException extends ApiException {

    private static final String DEFAULT_MESSAGE = "User has not been authenticated.";


    public AuthenticationException(){

        super(DEFAULT_MESSAGE);
    }

    public AuthenticationException(String message){

        super(message == null? DEFAULT_MESSAGE : message);
    }

    public AuthenticationException(String message, String code){
        this(message);
        setCode(code);
    }

    public AuthenticationException(Messages message){

        this(message.getMessage(), message.getCode());
    }


}
