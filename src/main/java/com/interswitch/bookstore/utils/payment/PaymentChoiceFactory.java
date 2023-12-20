package com.interswitch.bookstore.utils.payment;

import com.interswitch.bookstore.exceptions.Messages;
import com.interswitch.bookstore.exceptions.PaymentException;
import com.interswitch.bookstore.utils.payment.transfer.TransferServiceFactory;
import com.interswitch.bookstore.utils.payment.web.PaymentGatewayServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentChoiceFactory {

    private final PaymentGatewayServiceFactory paymentGatewayServiceFactory;
    private final TransferServiceFactory transferServiceFactory;

    @Autowired
    public PaymentChoiceFactory(PaymentGatewayServiceFactory paymentGatewayServiceFactory, TransferServiceFactory transferServiceFactory){
        this.paymentGatewayServiceFactory = paymentGatewayServiceFactory;
        this.transferServiceFactory = transferServiceFactory;
    }

    public PaymentInterface getPaymentChoiceImplementation(PaymentOption option){
        switch(option){
            case WEB -> {
                return paymentGatewayServiceFactory.getInstance();
            }
            case USSD -> {

            }
            case TRANSFER -> {
                transferServiceFactory.getInstance();
            }
        }
        throw new PaymentException(Messages.PAYMENT_INVALID_PAYMENT_METHOD);
    }

}
