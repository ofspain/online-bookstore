package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentResponse;
import com.interswitch.bookstore.utils.payment.web.PaymentGatewayInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("mock_payment_gateway")
public class MockPaymentGatewayService implements PaymentGatewayInterface {

    @Value("${mock.gateway.public.key}")
    private String publicKey;

    @Value("${mock.gateway.secrete.key}")
    private String secreteKey;



    @Override
    public PaymentDetails initialize(ShoppingCart shoppingCart) {
        PaymentDetails paymentDetails = new PaymentDetails(shoppingCart, true, "WEB");
        paymentDetails.setDetails(new HashMap<>(){{
            put("public_key",publicKey);
        }});
        return paymentDetails;
    }


    @Override
    public PaymentResponse processPayment(PaymentDetails paymentDetails) throws PaymentException {

        Map<String,String> cardMap = new HashMap<>(){{
            put("first_6digits", "553188");
            put("last_4digits", "2950");
            put("issuer", "CREDIT");
            put("country", "NIGERIA NG");
            put("type", "MASTERCARD");
            put("expiry_date", "09/22");
        }};
        double decider = Math.random();
        PaymentResponse response = new PaymentResponse();
        String desc = paymentDetails.getShoppingCart().generateDescription();
        Map<String,Object> payload = new HashMap<>();
        String jsonPayload = "{}";
        if(decider <= 0.85){
                payload = new HashMap<>(){{
                put("description", desc);
                put("amount", paymentDetails.getAmount());
                put("date", new Date().toString());
                put("status", "Successful");
                put("reference", paymentDetails.getReference());
                put("card_details", cardMap);
            }};

            response.setPaymentStatus(PaymentResponse.PaymentStatus.SUCCESSFUL);


        }else if(decider <= 0.95){
            payload = new HashMap<>(){{
                put("description", desc);
                put("amount", paymentDetails.getAmount());
                put("date", new Date().toString());
                put("status", "Failed");
                put("failure_reason", (Math.random()>0.5 ? "Bank deny access":"Insufficient Balance"));
                put("reference", paymentDetails.getReference());
                put("card_details", cardMap);
            }};
            response.setPaymentStatus(PaymentResponse.PaymentStatus.FAILED);
        }else{
            response.setPaymentStatus(PaymentResponse.PaymentStatus.PENDING);
        }
        try{
            jsonPayload = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        response.setJsonResponse(jsonPayload);
        return response;
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }


    @Override
    public String getServiceId() {
        return "mock_payment_gateway";
    }

}
