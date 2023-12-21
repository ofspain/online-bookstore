package com.interswitch.bookstore.utils.payment.ussd;

import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.utils.payment.PaymentInterface;

import java.util.List;

public abstract  class  USSDServiceInterface implements PaymentInterface {

    protected abstract List<BankAccountDetail> getBanksToPayTo();

    protected String getTransactionPrefix(){
        return "USSD";
    }
}

