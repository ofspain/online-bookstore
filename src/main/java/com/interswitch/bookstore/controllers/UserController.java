package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.dtos.PurchaseHistory;
import com.interswitch.bookstore.services.ShoppingCartService;
import com.interswitch.bookstore.utils.api.ApiResponse;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping
    public PaginateApiResponse findUserPurchaseHistory(@RequestParam(name = "page_number", defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(name = "page_size", defaultValue = "30") Integer pageSize){

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        return shoppingCartService.findUserPurchaseHistory(pageable);
    }
}
