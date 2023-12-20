package com.interswitch.bookstore.validators;

import com.interswitch.bookstore.models.Book;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

public class YearValidatorImpl  implements ConstraintValidator<YearValidator, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {

        if(null == value){
            return false;
        }
        int currentYear = LocalDate.now().getYear();
        int yearBack = currentYear - Book.MAX_YEAR_BACK;
        return value >= yearBack  && value <= currentYear;
    }
}
