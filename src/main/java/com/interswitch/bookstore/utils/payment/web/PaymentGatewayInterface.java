package com.interswitch.bookstore.utils.payment.web;

import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentInterface;
import com.interswitch.bookstore.utils.payment.PaymentResponse;

public abstract class PaymentGatewayInterface implements PaymentInterface {


    /**
     * Retrieve the secret key required by the backend to process payment.
     *
     * @return Secret key.
     */
   protected abstract String getPublicKey();

    protected String generateTransactionPrefix(){
        return "WEB";
    }

}

