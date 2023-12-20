package com.interswitch.bookstore.repositories;

import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.CartStatus;
import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Page<ShoppingCart> findByStatus(CartStatus status, Pageable pageable);

    Page<ShoppingCart> findByStatusAndUser(CartStatus status, User user, Pageable pageable);
}
