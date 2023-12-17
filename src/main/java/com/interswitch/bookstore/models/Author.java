package com.interswitch.bookstore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Author extends SuperModel{

    @Column(unique = true)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\s\\-]*$")
    private String name;

    @OneToMany
    private List<Book> publishBooks;
}
