package com.interswitch.bookstore;

import com.interswitch.bookstore.dtos.AddToCartDTO;
import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.CartItem;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.services.AuthorService;
import com.interswitch.bookstore.services.BookService;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.services.ShoppingCartService;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = TestConfig.class)
public class ServiceTest {
    @Autowired
    private IdempotentService idempotentService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Test
    public void testIdempotency() {
        Map<String,String> data = new HashMap<String,String>(){{
            put("1","one");
            put("2","two");
            put("3","three");
        }};
        ApiResponse<Map<String,String>> response = new ApiResponse<>(data, HttpStatus.OK);
        String randKey = BasicUtil.generateRandomAlphet(20).toLowerCase();
        idempotentService.saveResponse(randKey, response);

        assertNotNull(idempotentService.getResponse(randKey));
        assertNull(idempotentService.getResponse(BasicUtil.generateRandomAlphet(20).toLowerCase()));
    }

    @Test
    public void testSaveAuthor(){
        Author author = TestUtils.createAuthor();

        assertNull(author.getId(), "author created without id initially");
        author = authorService.saveAuthor(author);

        assertNotNull(author.getId(), "author saved succesfully");

    }

    @Test
    public void testSaveBook(){
        Book book = TestUtils.createBook();
        assertNull(book.getId(), "Book created initially without id");

        Author author = TestUtils.createAuthor();
        author = authorService.saveAuthor(author);
        book.setAuthor(author);
        book = bookService.saveBook(book);
        assertNotNull(book.getId(), "Book saved successfully");
    }

    @Test
    void testAddItemsToCart() {
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

        ShoppingCart initialCart = new ShoppingCart();

        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(books.get(0));
        cartItem1.setQuantity(3);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(books.get(1));
        cartItem2.setQuantity(5);

        CartItem cartItem3 = new CartItem();
        cartItem3.setBook(books.get(2));
        cartItem3.setQuantity(3);


        CartItem cartItem4 = new CartItem();
        cartItem4.setBook(books.get(3));
        cartItem4.setQuantity(1);

        CartItem cartItem5 = new CartItem();
        cartItem5.setBook(books.get(4));
        cartItem5.setQuantity(3);

        CartItem cartItem6 = new CartItem();
        cartItem6.setBook(books.get(4));
        cartItem6.setQuantity(4);


        List<CartItem> initialCartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);

        AddToCartDTO addToCartDTO = new AddToCartDTO(initialCart, initialCartItems);

        ShoppingCart updatedCart = shoppingCartService.addItemsToCart(addToCartDTO);




        assertNotNull(updatedCart.getCartItems());
        assertEquals(initialCart.getCartItems(), updatedCart.getCartItems());

        List<CartItem> updatedCartItems = updatedCart.getCartItems();
        assertNotNull(updatedCartItems);
        assertEquals(3, updatedCartItems.size());


        List<CartItem> laterCartItems = Arrays.asList(cartItem4, cartItem5, cartItem6);
        AddToCartDTO updateCart = new AddToCartDTO(updatedCart, laterCartItems);
        ShoppingCart laterShopping = shoppingCartService.addItemsToCart(updateCart);


        //assert merging do not duplicate carts: ex: cart to merge has two duplicate books,
        assertEquals(laterCartItems.size()-1, laterShopping.getCartItems().size()-3);

        System.out.println(laterShopping.getCartItems());

    }
}
