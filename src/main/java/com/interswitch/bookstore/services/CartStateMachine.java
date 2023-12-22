package com.interswitch.bookstore.services;

import com.interswitch.bookstore.exceptions.InconsistentException;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.models.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CartStateMachine {

    @Autowired
    private ShoppingCartService cartService;

    public ShoppingCart transition(ShoppingCart cart, CartStatus newStatus) {
        log.info("Transition cart with ref {} to {}",cart.getTransactionReference(), newStatus.toString());
        if (!isValidTransition(cart.getStatus(), newStatus)) {
            throw new InconsistentException("Invalid state transition from " + cart.getStatus() + " to " + newStatus, "3003");
        }

        cart.setStatus(newStatus);
        return cartService.saveShoppingCart(cart);
    }

    private boolean isValidTransition(CartStatus oldStatus, CartStatus newStatus) {

        if(oldStatus.equals(newStatus)){
            return true;
        }

        switch (oldStatus) {
            case FAILED, PROCESSED -> {
                return false;
            }
            case PENDING ->{
                return newStatus.equals(CartStatus.FAILED) || newStatus.equals(CartStatus.PROCESSED);
            }
            case ONGOING ->{
                return newStatus.equals(CartStatus.FAILED)
                    || newStatus.equals(CartStatus.PROCESSED)
                    || newStatus.equals(CartStatus.PENDING);
            }
        }

        return false;
    }
}