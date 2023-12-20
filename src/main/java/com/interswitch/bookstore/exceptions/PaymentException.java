package com.interswitch.bookstore.exceptions;

public class PaymentException extends ApiException{

    private static final String DEFAULT_MESSAGE = "Payment process not conclusive.";


    public PaymentException(){

        super(DEFAULT_MESSAGE);
    }

    public PaymentException(String message){

        super(message == null? DEFAULT_MESSAGE : message);
    }

    public PaymentException(String message, String code){
        this(message);
        setCode(code);
    }

    public PaymentException(Messages message){

        this(message.getMessage(), message.getCode());
    }
}
