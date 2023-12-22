package com.interswitch.bookstore.utils.payment;

import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.utils.BasicUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PaymentDetails {

    @NotNull
    private ShoppingCart shoppingCart;

    private String reference;

    @NotNull
    private Double amount;

    @NotNull
    private PaymentOption paymentOption;


    private Map<String,Object> details = new HashMap<>();

    public PaymentDetails(){}


    public PaymentDetails(ShoppingCart shoppingCart, PaymentOption paymentOption,
                          boolean expressCostInLowestDenomination, String referencePrefix){

        this.shoppingCart = shoppingCart;
        amount = shoppingCart.calculateShoppingCost(expressCostInLowestDenomination);
        this.reference = referencePrefix +"|"+ BasicUtil.generateTranReferenceSuffix();
        this.paymentOption = paymentOption;
    }
}
