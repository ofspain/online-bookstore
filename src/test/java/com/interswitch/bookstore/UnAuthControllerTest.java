package com.interswitch.bookstore;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.services.AuthorService;
import com.interswitch.bookstore.services.BookService;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.utils.BasicUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class UnAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;


    @Test
    public void testCreateUserEndpoint() throws Exception {
        User request = TestUtils.createUser();

        HttpHeaders headers = new HttpHeaders();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);


        MvcResult result = mockMvc.perform(post("/api/auth/sign-up")
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
    }

    @Test
    public void testCreateUserEndpointIdempotency() throws Exception {
        User request = TestUtils.createUser();

        HttpHeaders headers = new HttpHeaders();
        headers.set(IdempotentService.IDEMPOTENT_KEY, BasicUtil.generateRandomAlphet(12).toLowerCase());

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);


        MvcResult result = mockMvc.perform(post("/api/auth/sign-up")
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


        MvcResult result2 = mockMvc.perform(post("/api/auth/sign-up")
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
    }

    @Test
    public void testSearchBook() throws Exception{
        Author author = authorService.saveAuthor(TestUtils.createAuthor());
        Book book = TestUtils.createBook();
        book.setAuthor(author);
        bookService.saveBook(book);


        HttpHeaders headers = new HttpHeaders();

        MvcResult result = mockMvc.perform(get("/api/books?price=lte:"+book.getPrice())
                .headers(headers).contentType(MediaType.APPLICATION_JSON)).andReturn();

        String responseContent = result.getResponse().getContentAsString();

        try {
            Map<String,Object> response = new ObjectMapper().readValue(responseContent, Map.class);

            assertEquals("Request must be successful", "00", String.valueOf(response.get("status")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
