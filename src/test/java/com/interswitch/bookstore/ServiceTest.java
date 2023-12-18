package com.interswitch.bookstore;

import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = TestConfig.class)
public class ServiceTest {
    @Autowired
    private IdempotentService idempotentService;

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
}
