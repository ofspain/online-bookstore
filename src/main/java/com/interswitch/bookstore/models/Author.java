package com.interswitch.bookstore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="AUTHORS")
public class Author extends SuperModel{

    @Column(unique = true)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\s\\-]*$")
    private String name;

    @OneToMany
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Book> publishBooks = new ArrayList<>();
}
