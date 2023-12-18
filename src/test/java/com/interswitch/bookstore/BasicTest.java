package com.interswitch.bookstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.validators.YearValidatorImpl;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = TestConfig.class)
public class BasicTest {

    @Autowired
    private YearValidatorImpl yearValidator;

    @Autowired
    private Validator validator;


    @Test
    public void testValidRange() {
       // YearValidatorImpl validator = new YearValidatorImpl();

        assertTrue(yearValidator.isValid(2000, mock(ConstraintValidatorContext.class)));

        assertFalse(yearValidator.isValid(1800, mock(ConstraintValidatorContext.class)));

        assertFalse(yearValidator.isValid(null, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testYearValidator() {
        // Set up test data
        Book book = new Book();
        book.setYearOfPublication(1800);
        book.setAuthor(new Author());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

         assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserValidility(){
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User with no property is invalid");

        user.setPassword("ooooo");
        user.setFullName("akin ade");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User without phone and email is not valid");

        user.setPhoneNumber("07052972261");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User with phone only is valid");

        user.setEmail("email@domain.com");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User with phone and email is valid");

        user.setPhoneNumber(null);
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User with email only is valid");

        user.setPhoneNumber("09876543");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Invalid phone");

        user.setPhoneNumber("+2347052972261");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid phone");

    }


}
