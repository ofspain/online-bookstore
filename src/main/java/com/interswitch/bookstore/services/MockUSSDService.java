package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import com.interswitch.bookstore.utils.payment.PaymentResponse;
import com.interswitch.bookstore.utils.payment.ussd.USSDServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayTo;

@Slf4j
@Service("mock_ussd_service")
public class MockUSSDService  extends USSDServiceInterface {

    @Autowired
    private CartStateMachine cartStateMachine;

    @Override
    public PaymentDetails initialize(InitializePaymentDTO initializePaymentDTO) {
        BankDetail bankDetail = (BankDetail) initializePaymentDTO.getDetails().get("user_bank_details");
        System.out.println("User bank details "+bankDetail);

        PaymentDetails paymentDetails = new PaymentDetails(initializePaymentDTO
                .getShoppingCart(), PaymentOption.USSD,false, getTransactionPrefix());

        //call ussd end point with required details e.g bank and sort cord,amount etc to generate unique ussd code for this transaction
        //intercept response to construct need parameters for payment e.g code to dial etc
        String ussdCode = "*777*0000*R3009#";
        paymentDetails.setDetails(new HashMap<>(){{
            put("ussd_code", ussdCode);
            put("validity", 24);
        }});
        log.info("Initializing ussd payment with code {} ",ussdCode);
        return paymentDetails;
    }

    @Override
    public PaymentResponse processPayment(PaymentDetails paymentDetails) throws PaymentException {
        log.info("Making payment with reference {}",paymentDetails.getReference());
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
    protected List<BankAccountDetail> getBanksToPayTo() {

        return bankOptionToPayTo();
    }

    @Override
    public String getServiceId() {

        return "mock_ussd_service";
    }

    @Override
    public void requery(ShoppingCart shoppingCart) {
        log.info("Requering for ussd {}",shoppingCart.getTransactionReference());
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
