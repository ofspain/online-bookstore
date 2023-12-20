package com.interswitch.bookstore.utils.payment.web;

import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentInterface;
import com.interswitch.bookstore.utils.payment.PaymentResponse;

public interface PaymentGatewayInterface extends PaymentInterface {


    /**
     * Retrieve the secret key required by the backend to process payment.
     *
     * @return Secret key.
     */
    String getPublicKey();

}

