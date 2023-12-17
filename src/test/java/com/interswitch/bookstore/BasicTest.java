package com.interswitch.bookstore;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.validators.YearValidatorImpl;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
