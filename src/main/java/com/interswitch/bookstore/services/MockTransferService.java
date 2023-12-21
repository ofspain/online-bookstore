package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentResponse;
import com.interswitch.bookstore.utils.payment.transfer.TransferServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayFrom;
import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayTo;

@Service("mock_transfer_service")
public class MockTransferService extends TransferServiceInterface {

    @Autowired
    private CartStateMachine cartStateMachine;


    @Override
    public PaymentDetails initialize(InitializePaymentDTO initializePaymentDTO) {
        PaymentDetails paymentDetails = new PaymentDetails(initializePaymentDTO.getShoppingCart(),
                true, generateTransactionPrefix());
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
    protected List<BankAccountDetail> getBanksToPayTo() {

        return bankOptionToPayTo();
    }

    @Override
    protected List<BankDetail> getBanksToPayFrom() {

        return bankOptionToPayFrom();
    }

    @Override
    public void requery(ShoppingCart shoppingCart) {
        double decider = Math.random();
        CartStatus newStatus = CartStatus.PENDING;
        PaymentResponse.PaymentStatus paymentStatus = PaymentResponse.PaymentStatus.SUCCESSFUL;
        if(decider <= 0.85){
            paymentStatus = PaymentResponse.PaymentStatus.SUCCESSFUL;

        }else if(decider <= 0.95){
            paymentStatus = PaymentResponse.PaymentStatus.FAILED;
        }else{
            paymentStatus = PaymentResponse.PaymentStatus.PENDING;;
        }
        switch (paymentStatus){
            case SUCCESSFUL-> {newStatus = CartStatus.PROCESSED;}
            case FAILED -> {newStatus = CartStatus.FAILED;}
            default -> {newStatus = CartStatus.PENDING;}
        }


        if(newStatus.equals(CartStatus.PROCESSED)){
            shoppingCart.setDatePaid(new Date());
        }

        cartStateMachine.transition(shoppingCart, newStatus);

    }

}
