package com.interswitch.bookstore.dtos;


import lombok.Data;

@Data
public class BankAccountDetail {
    private String accName;
    private String accNumber;
    private BankDetail bankDetail;
}
