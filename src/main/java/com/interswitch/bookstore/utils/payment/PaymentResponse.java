package com.interswitch.bookstore.utils.payment;

import lombok.Data;

@Data
public class PaymentResponse {
    private PaymentStatus paymentStatus;
    private String jsonResponse;

    public enum PaymentStatus{
        SUCCESSFUL,
        FAILED,
        PENDING;
    }
}
