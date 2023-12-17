package com.interswitch.bookstore.models;

import com.interswitch.bookstore.validators.YearValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class Book extends SuperModel{


    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(unique = true)
    private String title;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Pattern(regexp = "^[0-9\\-]*$")
    @Column(unique = true)
    private String isbn;

    @YearValidator
    private Integer yearOfPublication;

   // @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

}