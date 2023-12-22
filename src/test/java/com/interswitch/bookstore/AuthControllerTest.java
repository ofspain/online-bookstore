package com.interswitch.bookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.dtos.BankDetail;
import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.models.*;
import com.interswitch.bookstore.services.*;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private CartStateMachine cartStateMachine;

    private String token;
    private User loggedUser;


    @Test
    public void testCacheShoppingCart() throws Exception {
        ShoppingCart request = generateShoppingCart();
        HttpHeaders headers = new HttpHeaders();
        headers.set(IdempotentService.IDEMPOTENT_KEY, BasicUtil.generateRandomAlphet(12).toLowerCase());

        headers.add("Authorization", "Bearer "+token);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);


        MvcResult result = mockMvc.perform(post("/api/shopping/save-ongoing")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        try {
            Map<String,Object> response = objectMapper.readValue(responseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }


        MvcResult result2 = mockMvc.perform(post("/api/shopping/save-ongoing")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String responseContent2 = result2.getResponse().getContentAsString();

        try {
            Map<String,Object> response2 = objectMapper.readValue(responseContent2, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response2.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }

        MvcResult resultChache = mockMvc.perform(get("/api/shopping/get-ongoing-saved")
                .headers(headers).contentType(MediaType.APPLICATION_JSON)).andReturn();

        String responseContentChache = resultChache.getResponse().getContentAsString();

        try {
            Map<String,Object> response = new ObjectMapper().readValue(responseContentChache, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckingOutInitialization() throws Exception{

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


        InitializePaymentDTO transferRequest = new InitializePaymentDTO(cart1, PaymentOption.TRANSFER);
        InitializePaymentDTO webRequest = new InitializePaymentDTO(cart2, PaymentOption.WEB);


        Map<String, Object> details = new HashMap<>(){{
            put("user_bank_details", new BankDetail("ACCESS BANK","0098"));
        }};

        InitializePaymentDTO ussdRequest = new InitializePaymentDTO(cart3,details,PaymentOption.USSD);

        HttpHeaders headers = new HttpHeaders();
        headers.set(IdempotentService.IDEMPOTENT_KEY, BasicUtil.generateRandomAlphet(12).toLowerCase());

        headers.add("Authorization", "Bearer "+token);



        ObjectMapper objectMapper = new ObjectMapper();
        String transferRequestBody = objectMapper.writeValueAsString(transferRequest);
        String webRequestBody = objectMapper.writeValueAsString(webRequest);
        String ussdRequestBody = objectMapper.writeValueAsString(ussdRequest);


        MvcResult transferResult = mockMvc.perform(post("/api/shopping/initialize-payment")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String transferResponseContent = transferResult.getResponse().getContentAsString();

        MvcResult webResult = mockMvc.perform(post("/api/shopping/initialize-payment")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String webResponseContent = webResult.getResponse().getContentAsString();

        MvcResult ussdResult = mockMvc.perform(post("/api/shopping/initialize-payment")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ussdRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String ussdResponseContent = ussdResult.getResponse().getContentAsString();

        try {
            Map<String,Object> response = objectMapper.readValue(transferResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));


            response = objectMapper.readValue(webResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));


            response = objectMapper.readValue(ussdResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //ApiResponse<PaymentDetails>
    }


    @Test
    public void testCheckingOutPayment() throws Exception{

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



        Map<String, Object> details = new HashMap<>(){{
            put("user_bank_details", new BankDetail("ACCESS BANK","0098"));
        }};




        PaymentDetails transferRequest = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart1,PaymentOption.TRANSFER));
        PaymentDetails webRequest = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart2,PaymentOption.WEB));
        PaymentDetails ussdRequest = checkoutService.setupPaymentEnvironment(new InitializePaymentDTO(cart3,details,PaymentOption.USSD));


        HttpHeaders headers = new HttpHeaders();
        headers.set(IdempotentService.IDEMPOTENT_KEY, BasicUtil.generateRandomAlphet(12).toLowerCase());

        headers.add("Authorization", "Bearer "+token);



        ObjectMapper objectMapper = new ObjectMapper();
        String transferRequestBody = objectMapper.writeValueAsString(transferRequest);
        String webRequestBody = objectMapper.writeValueAsString(webRequest);
        String ussdRequestBody = objectMapper.writeValueAsString(ussdRequest);


        MvcResult transferResult = mockMvc.perform(post("/api/shopping/checkout")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String transferResponseContent = transferResult.getResponse().getContentAsString();

        MvcResult webResult = mockMvc.perform(post("/api/shopping/checkout")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String webResponseContent = webResult.getResponse().getContentAsString();

        MvcResult ussdResult = mockMvc.perform(post("/api/shopping/checkout")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ussdRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.containsString("id:null"))))
                .andReturn();

        String ussdResponseContent = ussdResult.getResponse().getContentAsString();

        try {
            Map<String,Object> response = objectMapper.readValue(transferResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));


            response = objectMapper.readValue(webResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));


            response = objectMapper.readValue(ussdResponseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //ApiResponse<PaymentDetails>
    }

    @Test
    public void testPurchaseHistory(){
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
        cart1.setPaymentOption(PaymentOption.WEB);
        cart1.setUser(loggedUser);
        cart1.setDatePaid(new Date());
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
        cart2.setPaymentOption(PaymentOption.TRANSFER);
        cart2.setUser(loggedUser);
        cart2.setDatePaid(new Date());
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
        cart3.setPaymentOption(PaymentOption.USSD);
        cart3.setUser(loggedUser);
        cart3.setDatePaid(new Date());
        cart3 = cartStateMachine.transition(cart3,newStatus);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+token);

        try {
            MvcResult result = mockMvc.perform(get("/api/users")
                    .headers(headers).contentType(MediaType.APPLICATION_JSON)).andReturn();
            String responseContent = result.getResponse().getContentAsString();
            Map<String,Object> response = new ObjectMapper().readValue(responseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @BeforeEach
    void login(){
        User user = TestUtils.createUser();
        String password = user.getPassword();
        user = userService.saveUser(user);
        loggedUser = user;
        token = userService.login(user.getUsername(), password).getToken();

    }

    private ShoppingCart generateShoppingCart(){
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

        ShoppingCart shoppingCart = new ShoppingCart();
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
        shoppingCart.setCartItems(cartItems);

        return shoppingCart;

    }
}
