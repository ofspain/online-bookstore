package com.interswitch.bookstore.models;

import com.interswitch.bookstore.utils.payment.PaymentOption;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="shopping_carts")
public class ShoppingCart extends SuperModel{

    @ManyToOne
    private User user;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.ONGOING;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_paid")
    private Date datePaid;

    public double calculateShoppingCost(boolean expressInLowestDenomination){
        double cost = 0.0;

        for(CartItem cartItem : cartItems)
            cost += cartItem.calculateAmount();

        return expressInLowestDenomination ? (cost * 100) : cost;
    }

    public String generateDescription(){
        //craft description based on cartitems here

        return "purchasing items from booking store";
    }

}