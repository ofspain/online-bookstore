package com.interswitch.bookstore.repositories;

import com.interswitch.bookstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u  where u.phoneNumber = :uName or u.email = :uName")
    User findUserByUsername(@Param("uName") String username);
}
