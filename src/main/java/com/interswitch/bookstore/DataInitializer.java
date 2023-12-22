package com.interswitch.bookstore;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.Genre;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.security.JwtTokenProvider;
import com.interswitch.bookstore.services.*;
import com.interswitch.bookstore.utils.BasicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartStateMachine cartStateMachine;

    @Autowired
    private MockTransferService transferService;

    @Autowired
    private MockPaymentGatewayService paymentGatewayService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Override
    public void run(String... args) throws Exception {
        final String password = "abcd123kl90";

        List<Author> authors = saveAuthor();
        saveUsers(password);
        saveBooks(authors);

    }

    public List<Author> saveAuthor(){
        List<Author> authors = new ArrayList<>();
        for(int i=0; i<10; i++){
            Author author = new Author();
            author.setName((BasicUtil.generateRandomAlphet(5) +" "+BasicUtil.generateRandomAlphet(5)).toUpperCase());
            authors.add(authorService.saveAuthor(author));
        }
        return authors;
    }

    public List<Book> saveBooks(List<Author> authors){
        List<Book> books = new ArrayList<>();
        for(int i=0; i<50; i++){
            Book book = new Book();
            book.setGenre(Genre.findRandomGenre());
            book.setIsbn((BasicUtil.generateRandomNumeric(4) +"-"+BasicUtil.generateRandomNumeric(6)).toUpperCase());

            Random random = new Random();
            double price = 2000 + (40000 - 2000) * random.nextDouble();
            price = Math.round(price * 100.0) / 100.0;
            book.setPrice(price);

            book.setYearOfPublication(1980 + random.nextInt(2023 - 1980 + 1));

            book.setTitle(BasicUtil.generateRandomAlphaNumeric(25).toUpperCase());
            book.setAuthor(authors.get(random.nextInt(authors.size())));
            books.add(bookService.saveBook(book));
        }

        return books;

    }

    private List<User> saveUsers(String password){
        List<User> users = new ArrayList<>();
        for(int i=0; i<5;i++){
            User user = new User();
            user.setFullName(BasicUtil.generateRandomAlphet(5) +" "+BasicUtil.generateRandomAlphet(12));
            user.setEmail(BasicUtil.generateRandomAlphet(6)+"@domain.com");
            user.setPhoneNumber("+234"+BasicUtil.generateRandomNumeric(10));
            user.setPassword(password);

            users.add(userService.saveUser(user));
        }

        return users;
    }



}
