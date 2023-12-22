package com.interswitch.bookstore.dtos;

import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class InitializePaymentDTO {

    @NotNull
    private ShoppingCart shoppingCart;

    private Map<String,Object> details;

    @NotNull
    private PaymentOption paymentOption;

    public InitializePaymentDTO(){}

    public InitializePaymentDTO(ShoppingCart shoppingCart, Map<String,Object> details, PaymentOption paymentOption){
        this.shoppingCart = shoppingCart;
        this.details = details;
        this.paymentOption = paymentOption;
    }
    public InitializePaymentDTO(ShoppingCart shoppingCart, PaymentOption paymentOption){
        this(shoppingCart, new HashMap<>(), paymentOption);
    }
}
