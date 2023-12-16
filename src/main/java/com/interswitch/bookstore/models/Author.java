package com.interswitch.bookstore.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Author extends SuperModel{

    private String name;


    @OneToMany
    private List<Book> publishBooks;
}
