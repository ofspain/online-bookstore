package com.interswitch.bookstore.utils.payment.transfer;

import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.utils.payment.PaymentInterface;

import java.util.List;

public interface TransferServiceInterface extends PaymentInterface {

    List<BankAccountDetail> getBanksToPayTo();
    List<BankDetail> getBanksToPayFrom();


}

