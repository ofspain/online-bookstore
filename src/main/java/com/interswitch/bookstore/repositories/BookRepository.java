package com.interswitch.bookstore.repositories;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.Genre;
import com.interswitch.bookstore.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Integer countByIsbn(String isbn);

    Page<Book> findBookByAuthor(Author author, Pageable pageable);

    Page<Book> findBookByGenre(Genre genre, Pageable pageable);

    Page<Book> findBookByYearOfPublication(Integer year, Pageable pageable);
}
