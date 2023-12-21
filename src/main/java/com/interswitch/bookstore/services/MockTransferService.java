package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentResponse;
import com.interswitch.bookstore.utils.payment.transfer.TransferServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayFrom;
import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayTo;

@Service("mock_transfer_service")
public class MockTransferService implements TransferServiceInterface {


    @Override
    public PaymentDetails initialize(ShoppingCart shoppingCart) {
        PaymentDetails paymentDetails = new PaymentDetails(shoppingCart, true, "TRF");
        paymentDetails.setDetails(new HashMap<>(){{
            put("bank_options",bankOptionToPayTo());
        }});
        return paymentDetails;
    }

    @Override
    public PaymentResponse processPayment(PaymentDetails paymentDetails) throws PaymentException {

        double decider = Math.random();
        PaymentResponse response = new PaymentResponse();
        Map<String,Object> payload = new HashMap<>();
        String jsonPayload = "{}";
        String desc = paymentDetails.getShoppingCart().generateDescription();
        if(decider <= 0.75){
            payload = new HashMap<>(){{
                put("description", desc);
                put("amount", paymentDetails.getAmount());
                put("date", new Date().toString());
                put("status", "Successful");
                put("reference", paymentDetails.getReference());
            }};

            response.setPaymentStatus(PaymentResponse.PaymentStatus.SUCCESSFUL);


        }else if(decider <= 0.85){
            payload = new HashMap<>(){{
                put("description", desc);
                put("amount", paymentDetails.getAmount());
                put("date", new Date().toString());
                put("status", "Failed");
                put("failure_reason", (Math.random()>0.5 ? "Bank service unavailable":"Insufficient Balance"));
                put("reference", paymentDetails.getReference());
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
    public String getServiceId() {
        return "mock_transfer_service";
    }

    @Override
    public List<BankAccountDetail> getBanksToPayTo() {
        return bankOptionToPayTo();
    }

    @Override
    public List<BankDetail> getBanksToPayFrom() {

        return bankOptionToPayFrom();
    }
}
