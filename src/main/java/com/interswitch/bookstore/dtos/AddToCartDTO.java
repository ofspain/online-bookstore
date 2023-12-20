package com.interswitch.bookstore.dtos;

import com.interswitch.bookstore.models.CartItem;
import com.interswitch.bookstore.models.ShoppingCart;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddToCartDTO implements Serializable {

    @NotNull
    private ShoppingCart shoppingCart;

    @NotEmpty(message = "Cart must contain at least one Item")
    private List<CartItem> cartItems;
}
