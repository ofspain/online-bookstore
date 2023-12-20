package com.interswitch.bookstore.dtos;

import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class PurchaseHistory {

    private Date purchaseDate;
    private Double amount;
    private PaymentOption paymentOption;
    private Map<String, Integer> books;

}
