package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.dtos.IdempotenceDTO;
import com.interswitch.bookstore.dtos.InitializePaymentDTO;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.services.CheckoutService;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.services.ShoppingCartService;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import com.interswitch.bookstore.utils.payment.PaymentDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/shopping")
public class ShoppingController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private IdempotentService idempotentService;

    @Autowired
    private CheckoutService checkoutService;


    @PostMapping("/save-ongoing")
    public ApiResponse<Boolean> saveOngoingShoppingCart(@RequestBody ShoppingCart shoppingCart, @RequestHeader HttpHeaders headers){

        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<Boolean> cachedResponse = BasicUtil.validString(idemKey) ? (ApiResponse<Boolean>) idempotentService.getResponse(idemKey) : null;

        if(null != cachedResponse){
            return cachedResponse;
        }

        ApiResponse<Boolean> response = new ApiResponse<>(shoppingCartService.cacheOngoingShopping(shoppingCart));
        if(null != idemKey){
            idempotentService.saveResponse(idemKey, new IdempotenceDTO<Boolean>(response.getBody(), response.getHttpStatus()));
        }
        return response;
    }

    @GetMapping("/get-ongoing-saved")
    public ApiResponse<ShoppingCart> retrieveCachedShoppingCart(){
        return  new ApiResponse<>(shoppingCartService.retrieveCachedCart());

    }

    @PostMapping("/initialize-payment")
    public ApiResponse<PaymentDetails> setupPaymentEnvironment(@Valid @RequestBody InitializePaymentDTO initializePaymentDTO, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<PaymentDetails> cachedResponse = BasicUtil.validString(idemKey) ? (ApiResponse<PaymentDetails>) idempotentService.getResponse(idemKey) : null;

        if(null != cachedResponse){
            return cachedResponse;
        }

        ApiResponse<PaymentDetails> response = new ApiResponse<>(checkoutService.setupPaymentEnvironment(initializePaymentDTO));

        if(null != idemKey){
            idempotentService.saveResponse(idemKey, new IdempotenceDTO<PaymentDetails>(response.getBody(), response.getHttpStatus()));
        }
        return response;
    }

    @PostMapping("/checkout")
    public ApiResponse<ShoppingCart> checkout(@Valid @RequestBody PaymentDetails paymentRequest, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<ShoppingCart> cachedResponse = BasicUtil.validString(idemKey) ? (ApiResponse<ShoppingCart>) idempotentService.getResponse(idemKey) : null;

        if(null != cachedResponse){
            return cachedResponse;
        }

        ApiResponse<ShoppingCart> response = new ApiResponse<>(checkoutService.checkout(paymentRequest));

        if(null != idemKey){
            idempotentService.saveResponse(idemKey, new IdempotenceDTO<ShoppingCart>(response.getBody(), response.getHttpStatus()));
        }
        return response;
    }

    @GetMapping("/requery")
    public ApiResponse<ShoppingCart> getCurrentState(@RequestParam(name = "transaction_reference") String transactionRef){
        return new ApiResponse<>(shoppingCartService.getByReference(transactionRef));
    }
}
