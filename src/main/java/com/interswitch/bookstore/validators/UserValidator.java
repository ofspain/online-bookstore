package com.interswitch.bookstore.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidatorImpl.class)
public @interface UserValidator {
    String message() default "Phone number and Email can not be empty at the same time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
