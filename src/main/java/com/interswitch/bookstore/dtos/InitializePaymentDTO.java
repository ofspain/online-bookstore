package com.interswitch.bookstore.dtos;

import com.interswitch.bookstore.models.ShoppingCart;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class InitializePaymentDTO {

    @NotNull
    private ShoppingCart shoppingCart;

    private Map<String,Object> details;

    public InitializePaymentDTO(){}

    public InitializePaymentDTO(ShoppingCart shoppingCart, Map<String,Object> details){
        this.shoppingCart = shoppingCart;
        this.details = details;
    }
    public InitializePaymentDTO(ShoppingCart shoppingCart){
        this(shoppingCart, new HashMap<>());
    }
}
