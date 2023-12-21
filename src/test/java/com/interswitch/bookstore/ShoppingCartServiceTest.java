package com.interswitch.bookstore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.interswitch.bookstore.exceptions.InconsistentException;
import com.interswitch.bookstore.models.*;
import com.interswitch.bookstore.services.AuthorService;
import com.interswitch.bookstore.services.BookService;
import com.interswitch.bookstore.services.CartStateMachine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class ShoppingCartServiceTest {


    @Autowired
    private CartStateMachine cartStateMachine;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;


    @Test
    public void testValidTransition() {
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for(int i = 0; i<=3; i++){
            Author a = authorService.saveAuthor(TestUtils.createAuthor());
            authors.add(a);
        }
        for(int j=0; j<=5; j++){
            Book book = TestUtils.createBook();
            book.setAuthor(authors.get(new Random().nextInt(authors.size())));
            book = bookService.saveBook(book);

            books.add(book);
        }


        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(books.get(0));
        cartItem1.setQuantity(3);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(books.get(1));
        cartItem2.setQuantity(5);

        CartItem cartItem3 = new CartItem();
        cartItem3.setBook(books.get(2));
        cartItem3.setQuantity(3);

        ShoppingCart cart = new ShoppingCart();
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart.setCartItems(cartItems);

        CartStatus newStatus = CartStatus.PROCESSED;

        assertNull(cart.getId(), "shooping cart is still transient");
        cart = cartStateMachine.transition(cart,newStatus);
        assertEquals(newStatus, cart.getStatus());
        assertNotNull(cart.getId(), "cart state machines persist shopping cart to db");
    }

    @Test
    public void testInvalidTransition() {
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for(int i = 0; i<=3; i++){
            Author a = authorService.saveAuthor(TestUtils.createAuthor());
            authors.add(a);
        }
        for(int j=0; j<=5; j++){
            Book book = TestUtils.createBook();
            book.setAuthor(authors.get(new Random().nextInt(authors.size())));
            book = bookService.saveBook(book);

            books.add(book);
        }


        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(books.get(0));
        cartItem1.setQuantity(3);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(books.get(1));
        cartItem2.setQuantity(5);

        CartItem cartItem3 = new CartItem();
        cartItem3.setBook(books.get(2));
        cartItem3.setQuantity(3);

        ShoppingCart cart = new ShoppingCart();
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart.setCartItems(cartItems);
        cart.setStatus(CartStatus.PROCESSED);

        CartStatus newStatus = CartStatus.FAILED;

        assertThrows(InconsistentException.class, () -> cartStateMachine.transition(cart, newStatus));

    }
}
