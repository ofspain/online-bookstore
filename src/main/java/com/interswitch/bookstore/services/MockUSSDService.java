package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentResponse;
import com.interswitch.bookstore.utils.payment.ussd.USSDServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interswitch.bookstore.utils.BasicUtil.bankOptionToPayTo;

@Service("mock_ussd_service")
public class MockUSSDService  extends USSDServiceInterface {
    @Override
    public PaymentDetails initialize(InitializePaymentDTO initializePaymentDTO) {
        BankDetail bankDetail = (BankDetail) initializePaymentDTO.getDetails().get("user_bank_details");
        System.out.println("User bank details "+bankDetail);

        PaymentDetails paymentDetails = new PaymentDetails(initializePaymentDTO
                .getShoppingCart(), false, getTransactionPrefix());

        //call ussd end point with required details e.g bank and sort cord,amount etc to generate unique ussd code for this transaction
        //intercept response to construct need parameters for payment e.g code to dial etc
        paymentDetails.setDetails(new HashMap<>(){{
            put("ussd_code", "*777*0000*R3009#");
            put("validity", 24);
        }});
        return paymentDetails;
    }

    @Override
    public PaymentResponse processPayment(PaymentDetails paymentDetails) throws PaymentException {

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
}
