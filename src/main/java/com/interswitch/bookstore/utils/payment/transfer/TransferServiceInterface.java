package com.interswitch.bookstore.utils.payment.transfer;

import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.utils.payment.PaymentInterface;

import java.util.List;

public abstract class TransferServiceInterface implements PaymentInterface {

    protected abstract List<BankAccountDetail> getBanksToPayTo();
    protected abstract List<BankDetail> getBanksToPayFrom();

    protected String generateTransactionPrefix(){
        return "TRF";
    }

}

