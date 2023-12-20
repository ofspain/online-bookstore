package com.interswitch.bookstore.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interswitch.bookstore.validators.YearValidator;
import com.interswitch.bookstore.validators.YearValidatorImpl;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Data
@Table(name = "BOOKS")
public class Book extends SuperModel{

    public static final Integer MAX_YEAR_BACK = 400;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String title;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Pattern(regexp = "^[0-9\\-]*$")
    @Column(unique = true)
    @NotNull
    private String isbn;

    @NotNull
    private Double price;

    @YearValidator
    @Column(name = "year_of_pub")
    @JsonProperty("year_of_publication")
    private Integer yearOfPublication;

   // @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Override
    public boolean equals(Object other){
        if(other instanceof Book){
            Book otherBook = (Book) other;
            return otherBook.getId().equals(this.getId());
        }

        return false;
    }


}//title,genre,isbn, year_of_pub