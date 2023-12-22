package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class CheckoutService {


    private PaymentChoiceFactory choiceFactory;
    private CartStateMachine stateMachine;
    private UserService userService;

    @Autowired
    public CheckoutService(PaymentChoiceFactory choiceFactory, CartStateMachine stateMachine, UserService userService){
        this.choiceFactory = choiceFactory;
        this.stateMachine = stateMachine;
        this.userService = userService;
    }

    public ShoppingCart checkout(PaymentDetails paymentRequest) {

        log.info("checking out for {}",paymentRequest.getReference());

        PaymentOption paymentOption = paymentRequest.getPaymentOption();

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
        shoppingCart.setUser(userService.getAuthUser());
        shoppingCart.setTransactionReference(paymentRequest.getReference());
        shoppingCart = stateMachine.transition(shoppingCart, newStatus);

        return shoppingCart;
    }

    public PaymentDetails setupPaymentEnvironment(InitializePaymentDTO initializePaymentDTO){
        return choiceFactory.getPaymentChoiceImplementation(initializePaymentDTO.getPaymentOption()).initialize(initializePaymentDTO);
    }
}

