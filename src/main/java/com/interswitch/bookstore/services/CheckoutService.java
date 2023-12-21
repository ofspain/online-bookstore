package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CheckoutService {


    private PaymentChoiceFactory choiceFactory;
    private CartStateMachine stateMachine;

    @Autowired
    public CheckoutService(PaymentChoiceFactory choiceFactory, CartStateMachine stateMachine){
        this.choiceFactory = choiceFactory;
        this.stateMachine = stateMachine;
    }

    public ShoppingCart checkout(PaymentDetails paymentRequest, PaymentOption paymentOption) {

        PaymentResponse paymentResponse = choiceFactory.getPaymentChoiceImplementation(paymentOption)
                .processPayment(paymentRequest);

        PaymentResponse.PaymentStatus status = paymentResponse.getPaymentStatus();
        ShoppingCart shoppingCart = paymentRequest.getShoppingCart();
        CartStatus newStatus = shoppingCart.getStatus();
        switch(status){
            case FAILED ->{
                newStatus = CartStatus.FAILED;
            }
            case PENDING -> {
                newStatus = CartStatus.PENDING;
            }case SUCCESSFUL -> {
                newStatus = CartStatus.PROCESSED;
            }
        }
        shoppingCart.setPaymentOption(paymentOption);
        if(newStatus.equals(CartStatus.PROCESSED)){
            shoppingCart.setDatePaid(new Date());
        }
        shoppingCart = stateMachine.transition(shoppingCart, newStatus);

        return shoppingCart;
    }

    public PaymentDetails setupPaymentEnvironment(InitializePaymentDTO initializePaymentDTO, PaymentOption paymentOption){
        return choiceFactory.getPaymentChoiceImplementation(paymentOption).initialize(initializePaymentDTO);
    }
}

