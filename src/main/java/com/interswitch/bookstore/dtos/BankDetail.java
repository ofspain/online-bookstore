package com.interswitch.bookstore.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@JsonTypeName("bank_detail")
@NoArgsConstructor
public class BankDetail {

    private String code;

    private String name;

    public BankDetail(String name, String code){
        this.name = name;
        this.code = code;
    }
}
