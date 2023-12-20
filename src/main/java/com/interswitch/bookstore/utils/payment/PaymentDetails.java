package com.interswitch.bookstore.utils.payment;

import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.utils.BasicUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PaymentDetails {
    private ShoppingCart shoppingCart;
    private String reference;
    private double amount;


    private Map<String,Object> details = new HashMap<>();

    public PaymentDetails(){}


    public PaymentDetails(ShoppingCart shoppingCart, boolean expressCostInLowestDenomination, String referencePrefix){

        this.shoppingCart = shoppingCart;
        amount = shoppingCart.calculateShoppingCost(expressCostInLowestDenomination);
        this.reference = referencePrefix +"|"+ BasicUtil.generateTranReferenceSuffix();
    }
}
