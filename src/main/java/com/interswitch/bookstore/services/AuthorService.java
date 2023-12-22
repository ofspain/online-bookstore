package com.interswitch.bookstore.services;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.repositories.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Author saveAuthor(Author author){
        log.info("saving author {}", author.getName());
        return authorRepository.save(author);
    }
}
