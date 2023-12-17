package com.interswitch.bookstore.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearValidatorImpl.class)
public @interface YearValidator {
    String message() default "Value is not within acceptable range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
