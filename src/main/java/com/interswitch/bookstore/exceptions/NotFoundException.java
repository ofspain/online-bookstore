package com.interswitch.bookstore.exceptions;

public class NotFoundException  extends ApiException{
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Messages messageCode) {
        super(messageCode);
    }
}
