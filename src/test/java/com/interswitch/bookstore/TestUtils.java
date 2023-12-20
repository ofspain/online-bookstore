package com.interswitch.bookstore;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.Genre;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.utils.BasicUtil;

import java.util.Random;

public class TestUtils {

    public static User createUser(){
        User user = new User();
        user.setFullName(BasicUtil.generateRandomAlphet(5) +" "+BasicUtil.generateRandomAlphet(12));
        user.setEmail(BasicUtil.generateRandomAlphet(6)+"@domain.com");
        user.setPhoneNumber("+234"+BasicUtil.generateRandomNumeric(10));
        user.setPassword(BasicUtil.generateRandomAlphet(10));

        return user;
    }

    public static Book createBook(){
        Book book = new Book();
        book.setGenre(Genre.findRandomGenre());
        book.setIsbn((BasicUtil.generateRandomNumeric(4) +"-"+BasicUtil.generateRandomNumeric(6)).toUpperCase());

        Random random = new Random();
        double price = 2000 + (40000 - 2000) * random.nextDouble();
        price = Math.round(price * 100.0) / 100.0;
        book.setPrice(price);

        book.setYearOfPublication(1980 + random.nextInt(2023 - 1980 + 1));

        book.setTitle(BasicUtil.generateRandomAlphaNumeric(25).toUpperCase());

        return book;
    }

    public static Author createAuthor(){
        Author author = new Author();
        author.setName((BasicUtil.generateRandomAlphet(5) +" "+BasicUtil.generateRandomAlphet(5)).toUpperCase());

        return author;
    }

}
