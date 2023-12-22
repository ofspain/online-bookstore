package com.interswitch.bookstore;

import com.interswitch.bookstore.dtos.*;
import com.interswitch.bookstore.exceptions.AuthenticationException;
import com.interswitch.bookstore.models.*;
import com.interswitch.bookstore.security.JwtTokenProvider;
import com.interswitch.bookstore.services.*;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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


    @Test
    public void testIdempotency() {
        Map<String,String> data = new HashMap<String,String>(){{
            put("1","one");
            put("2","two");
            put("3","three");
        }};
        ApiResponse<Map<String,String>> response = new ApiResponse<>(data, HttpStatus.OK);
        String randKey = BasicUtil.generateRandomAlphet(20).toLowerCase();
        idempotentService.saveResponse(randKey, new IdempotenceDTO<Map<String,String>>(response.getBody(), HttpStatus.OK));

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
    public void testSaveUser(){
        User user = TestUtils.createUser();
        assertNull(user.getId(), "user in transient state");
        user = userService.saveUser(user);

        assertNotNull(user.getId(), "user persisted");
    }

    @Test
    public void testLogin(){
        User user = TestUtils.createUser();
        String plainPassword = user.getPassword();
        user = userService.saveUser(user);


        assertThrows(AuthenticationException.class, () -> userService.login("my@domain.com", plainPassword));

        User finalUser = user;
        assertThrows(AuthenticationException.class, () -> userService.login(finalUser.getUsername(), "wrong_password"));

        LoginDTO loginDTO = userService.login(user.getUsername(), plainPassword);
        String jwtToken = loginDTO.getToken();
        assertNotNull(jwtToken, "Auth token generated");

    }

    @Test
    public void testAddItemsToCart() {
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

    @Test
    public void testCachingShopping(){
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

        ShoppingCart cart = new ShoppingCart();
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userService.saveUser(TestUtils.createUser());


        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(books.get(0));
        cartItem1.setQuantity(3);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(books.get(1));
        cartItem2.setQuantity(5);

        CartItem cartItem3 = new CartItem();
        cartItem3.setBook(books.get(2));
        cartItem3.setQuantity(3);


        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart.setUser(user);
        cart.setCartItems(cartItems);
        boolean cached = shoppingCartService.cacheOngoingShopping(cart);
        assertTrue(cached,"caching successful");
        ShoppingCart retrievedCache = shoppingCartService.retrieveCachedCart();
//
       assertTrue(cart.getCartItems().equals(retrievedCache.getCartItems()));
    }

    @Test
    public void testPurchaseHistory(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for(int i = 0; i<=3; i++){
            Author a = authorService.saveAuthor(TestUtils.createAuthor());
            authors.add(a);
        }
        for(int j=0; j<=9; j++){
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

        ShoppingCart cart1 = new ShoppingCart();
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart1.setCartItems(cartItems);

        CartStatus newStatus = CartStatus.PROCESSED;
        cart1.setUser(user);
        cart1.setDatePaid(new Date());
        cart1.setPaymentOption(PaymentOption.WEB);

        cart1 = cartStateMachine.transition(cart1,newStatus);


        CartItem cartItem4 = new CartItem();
        cartItem4.setBook(books.get(3));
        cartItem4.setQuantity(3);

        CartItem cartItem5 = new CartItem();
        cartItem5.setBook(books.get(4));
        cartItem5.setQuantity(2);

        CartItem cartItem6 = new CartItem();
        cartItem6.setBook(books.get(5));
        cartItem6.setQuantity(2);

        ShoppingCart cart2 = new ShoppingCart();
        List<CartItem> cartItems2 = Arrays.asList(cartItem4, cartItem5, cartItem6);
        cart2.setCartItems(cartItems2);
        cart2.setUser(user);cart2.setDatePaid(new Date());
        cart2.setPaymentOption(PaymentOption.TRANSFER);

        cart2 = cartStateMachine.transition(cart2,newStatus);


        CartItem cartItem7 = new CartItem();
        cartItem7.setBook(books.get(3));
        cartItem7.setQuantity(3);

        CartItem cartItem8 = new CartItem();
        cartItem8.setBook(books.get(4));
        cartItem8.setQuantity(2);

        CartItem cartItem9 = new CartItem();
        cartItem9.setBook(books.get(5));
        cartItem9.setQuantity(2);

        ShoppingCart cart3 = new ShoppingCart();
        List<CartItem> cartItems3 = Arrays.asList(cartItem7, cartItem8, cartItem9);
        cart3.setCartItems(cartItems3);
        cart3.setUser(user);
        cart3.setDatePaid(new Date());
        cart3.setPaymentOption(PaymentOption.USSD);

        cart3 = cartStateMachine.transition(cart3,newStatus);


        PaginateApiResponse historyPage = shoppingCartService.findUserPurchaseHistory(PageRequest.of(0, 1));

        assertTrue(historyPage.getPaginationBody().getData().size() == 1);

        historyPage = shoppingCartService.findUserPurchaseHistory(PageRequest.of(0, 4));
        assertTrue(historyPage.getPaginationBody().getData().size() == 3);

        historyPage = shoppingCartService.findUserPurchaseHistory(PageRequest.of(3, 1));
        assertTrue(historyPage.getPaginationBody().getData().size() == 0);
    }

    @Test
    public void testInitializePayment(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userService.saveUser(TestUtils.createUser());
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for(int i = 0; i<=3; i++){
            Author a = authorService.saveAuthor(TestUtils.createAuthor());
            authors.add(a);
        }
        for(int j=0; j<=9; j++){
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

        ShoppingCart cart1 = new ShoppingCart();
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart1.setCartItems(cartItems);
        cart1.setUser(user);

        PaymentDetails transferPaymentDetails = transferService.initialize(new InitializePaymentDTO(cart1, PaymentOption.TRANSFER));
        PaymentDetails webPaymentDetails = paymentGatewayService.initialize(new InitializePaymentDTO(cart1,PaymentOption.WEB));

        assertTrue(transferPaymentDetails.getReference().startsWith("TRF"));
        assertTrue(webPaymentDetails.getReference().startsWith("WEB"));

        assertTrue(transferPaymentDetails.getDetails().containsKey("bank_options"));
        assertTrue(webPaymentDetails.getDetails().containsKey("public_key"));

    }

    @Test
    public void testCheckout(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userService.saveUser(TestUtils.createUser());
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for(int i = 0; i<=3; i++){
            Author a = authorService.saveAuthor(TestUtils.createAuthor());
            authors.add(a);
        }
        for(int j=0; j<=9; j++){
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

        ShoppingCart cart1 = new ShoppingCart();
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
        cart1.setCartItems(cartItems);
        cart1.setUser(user);

        ShoppingCart cart2 = new ShoppingCart();
        CartItem cartItem4 = new CartItem();
        cartItem4.setBook(books.get(0));
        cartItem4.setQuantity(3);

        CartItem cartItem5 = new CartItem();
        cartItem5.setBook(books.get(1));
        cartItem5.setQuantity(5);

        CartItem cartItem6 = new CartItem();
        cartItem6.setBook(books.get(2));
        cartItem6.setQuantity(3);

        List<CartItem> cartItems2 = Arrays.asList(cartItem4, cartItem5, cartItem6);
        cart2.setCartItems(cartItems2);
        cart2.setUser(user);


        ShoppingCart cart3 = new ShoppingCart();
        CartItem cartItem7 = new CartItem();
        cartItem7.setBook(books.get(0));
        cartItem7.setQuantity(3);

        CartItem cartItem8 = new CartItem();
        cartItem8.setBook(books.get(1));
        cartItem8.setQuantity(5);

        CartItem cartItem9 = new CartItem();
        cartItem9.setBook(books.get(2));
        cartItem9.setQuantity(3);

        List<CartItem> cartItems3 = Arrays.asList(cartItem7, cartItem8, cartItem9);
        cart3.setCartItems(cartItems3);
        cart3.setUser(user);



        PaymentDetails transferPaymentDetails = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart1,PaymentOption.TRANSFER));
        PaymentDetails webPaymentDetails = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart2,PaymentOption.WEB));

        Map<String, Object> details = new HashMap<>(){{
            put("user_bank_details", new BankDetail("ACCESS BANK","0098"));
        }};


        PaymentDetails ussdPaymentDetails = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart3,details,PaymentOption.USSD));

        ShoppingCart savedCart1 = checkoutService.checkout(transferPaymentDetails);
        ShoppingCart savedCart2 = checkoutService.checkout(webPaymentDetails);
        ShoppingCart savedCart3 = checkoutService.checkout(ussdPaymentDetails);

        assertTrue(transferPaymentDetails.getReference().startsWith("TRF"));
        assertTrue(webPaymentDetails.getReference().startsWith("WEB"));
        assertTrue(ussdPaymentDetails.getReference().startsWith("USSD"));

        assertTrue(transferPaymentDetails.getDetails().containsKey("bank_options"));
        assertTrue(webPaymentDetails.getDetails().containsKey("public_key"));
        assertTrue(ussdPaymentDetails.getDetails().containsKey("ussd_code"));


        assertNotNull(savedCart1.getId());
        assertNotNull(savedCart2.getId());
        assertNotNull(savedCart3.getId());

        assertNotEquals(savedCart1.getId(), savedCart2.getId());
        assertNotEquals(savedCart2.getId(), savedCart3.getId());
        assertNotEquals(savedCart1.getId(), savedCart3.getId());


    }

    @BeforeEach
    void login(){
        User user = TestUtils.createUser();
        String password = user.getPassword();
        user = userService.saveUser(user);

        String jwtToken = userService.login(user.getUsername(), password).getToken();

        Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}
