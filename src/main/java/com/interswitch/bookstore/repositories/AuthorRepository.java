package com.interswitch.bookstore.repositories;

import com.interswitch.bookstore.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByName(String name);
}
