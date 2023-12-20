package com.interswitch.bookstore.exceptions;

public class InconsistentException extends ApiException {

    private static final String DEFAULT_MESSAGE = "Process may trigger inconsistency on the system.";


    public InconsistentException(){

        super(DEFAULT_MESSAGE);
    }

    public InconsistentException(String message){

        super(message == null? DEFAULT_MESSAGE : message);
    }

    public InconsistentException(String message, String code){
        this(message);
        setCode(code);
    }

    public InconsistentException(Messages message){

        this(message.getMessage(), message.getCode());
    }
}
