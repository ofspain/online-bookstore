package com.interswitch.bookstore.services;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Author saveAuthor(Author author){
        return authorRepository.save(author);
    }
}
