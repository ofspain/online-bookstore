package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.services.BookService;
import com.interswitch.bookstore.utils.api.ApiResponse;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("")
    public ApiResponse<PaginateApiResponse> getFilteredBookSearch(HttpServletRequest request) {
        return new ApiResponse<>(bookService.searchBook(request));

    }
}
