package com.interswitch.bookstore.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class YearValidatorImpl  implements ConstraintValidator<YearValidator, Integer> {

    @Autowired
    private Environment environment;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if(null == value){
            return false;
        }
        String back = environment.getProperty("year.back");
        int currentYear = LocalDate.now().getYear();
        int yearBack = currentYear - Integer.parseInt(back);
        return value >= yearBack  && value <= currentYear;
    }
}
