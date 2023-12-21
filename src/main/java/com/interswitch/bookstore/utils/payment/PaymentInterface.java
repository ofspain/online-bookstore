package com.interswitch.bookstore.utils.payment;

import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.models.ShoppingCart;

public interface PaymentInterface {


    /**
     * Initialize the payment gateway.
     * @param shoppingCart The cart we are making payment for
     * @return InitializationResponse The response of this process
     */
    PaymentDetails initialize(InitializePaymentDTO initializePaymentDTO);

    /**
     * Process a payment transaction.
     *
     * @param paymentRequest Payment request containing necessary information.
     * @return Payment response indicating the status of the transaction.
     * @throws PaymentException If there's an issue during payment processing.
     */
    PaymentResponse processPayment(PaymentDetails paymentRequest) throws PaymentException;


    /**
     *
     * @return a unique string to identify this provider by the application
     */
    String getServiceId();
}

