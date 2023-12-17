package com.interswitch.bookstore;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.utils.api.ApiResponse;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import com.interswitch.bookstore.utils.api.PaginationBody;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
public class ApiResponseTests {


    @Test
    void testApiResponse(){
        TestDto dto = new TestDto("test", 2300);

        ApiResponse<TestDto> actualApiResponse = new ApiResponse<>(dto);
        assertNotNull("Body cannot be null", actualApiResponse.getBody());
        assertEquals("TestDto not found", dto, Objects.requireNonNull(actualApiResponse.getBody()).getData());
        assertTrue("String property does not match TestDto", dto.getString().equals(actualApiResponse.getBody().getData().getString()));
        assertEquals("HttpStatus is not correct", HttpStatus.OK, actualApiResponse.getStatusCode());
        assertNotEquals("HttpStatus is not correct", HttpStatus.INTERNAL_SERVER_ERROR, actualApiResponse.getStatusCode());

        //Test with meta response
        Map<String, Object> meta = new HashMap<>();
        meta.put("obj1", 1);
        meta.put("obj2", "smart");
        meta.put("obj3", 3L);

        actualApiResponse = new ApiResponse<>(dto, meta);
        assertNotNull("Meta cannot be null", actualApiResponse.getBody().getMeta());
        assertEquals("Meta is empty", meta.size(), actualApiResponse.getBody().getMeta().size());
        assertEquals("HttpStatus is not correct", HttpStatus.OK, actualApiResponse.getStatusCode());

        //Test validation errors
        Map<String, Object> validation = new HashMap<>();
        validation.put("number", "Invalid Number");
        validation.put("firstname", "Firstname cannot be empty");

        actualApiResponse = new ApiResponse<>(validation);
        assertNotNull("Meta cannot be null", actualApiResponse.getBody().getMeta());
        assertEquals("HttpStatus is not correct", HttpStatus.BAD_REQUEST, actualApiResponse.getStatusCode());

        //Test Body with custom HttpStatus
        actualApiResponse = new ApiResponse<>(dto, HttpStatus.CONTINUE);
        assertEquals("HttpStatus is not correct", HttpStatus.CONTINUE, actualApiResponse.getStatusCode());

        //Test message and custom HttpStatus
        actualApiResponse = new ApiResponse<>("Service Timeout", HttpStatus.GATEWAY_TIMEOUT);
        assertEquals("HttpStatus is not correct", HttpStatus.GATEWAY_TIMEOUT, actualApiResponse.getStatusCode());

        //Test message, custom status code and HttpStatus
        actualApiResponse = new ApiResponse<>("Processing request", "01", HttpStatus.PROCESSING);
        assertNotNull("Status cannot be null", actualApiResponse.getBody().getStatus());
        assertTrue("Status does not match", "01".equals(actualApiResponse.getBody().getStatus()));
        assertEquals("HttpStatus is not correct", HttpStatus.PROCESSING, actualApiResponse.getStatusCode());

        //Test custom headers
        MultiValueMap<String, String> headers = new HttpHeaders();
        List<String> john = new ArrayList<>();
        john.add("simple");
        john.add("s3");

        headers.put("John", john);

        actualApiResponse = new ApiResponse<>(headers, HttpStatus.CREATED);
        assertTrue("Header does not match", actualApiResponse.getHeaders().containsKey("John"));

    }

    @Test
    void testPaginateApiResponse(){

        List<TestDto> content = new ArrayList<>();
        content.add(new TestDto("test-data-1", 1));
        content.add(new TestDto("test-data-2", 2));
       // content.add(new TestDto("test-data-3", 3));

        Pageable paging = PageRequest.of(1, 10);
        Page<TestDto> result = new PageImpl<>(content, paging, 50);
        PaginationBody body = new PaginationBody<>(result, 50);

        PaginateApiResponse apiResponse = new PaginateApiResponse(body);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(apiResponse));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Data
    static class TestDto {

        private String string;
        private Integer number;

        TestDto(String string, Integer number) {
            this.string = string;
            this.number = number;
        }
    }
}
