package com.interswitch.bookstore.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="cart_items")
public class CartItem extends SuperModel{

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
//
//    @ManyToOne
//    @JoinColumn(name = "cart_id")
//    private ShoppingCart cart;

    private Integer quantity;

    @Override
    public boolean equals(Object other){
        if(other instanceof CartItem){
            CartItem otherItem = (CartItem) other;
            if(null != otherItem.getId() && null != this.getId()){
                return otherItem.getId().equals(this.getId());
            }else{
                return otherItem.getBook().equals(this.getBook());
            }
        }
        return false;
    }

    public double calculateAmount(){
        return quantity * book.getPrice();
    }

}
