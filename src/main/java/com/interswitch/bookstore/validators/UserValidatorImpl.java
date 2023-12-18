package com.interswitch.bookstore.validators;

import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.utils.BasicUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserValidatorImpl implements ConstraintValidator<UserValidator, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        return BasicUtil.validString(user.getEmail()) || BasicUtil.validString(user.getPhoneNumber());
    }
}
