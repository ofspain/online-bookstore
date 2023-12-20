package com.interswitch.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.interswitch.bookstore.deserializer.GrantedAuthorityDeserializer;
import com.interswitch.bookstore.validators.UserValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")
@UserValidator
public class User extends SuperModel implements UserDetails{

    @NotNull(message = "full name must be between 5 and 30 characters")
    @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters.")
    private String fullName;


    @Pattern(regexp = "^(0\\d{10}|\\+\\d{13})$", message = "Invalid phone")
    @Column(unique = true)
    private String phoneNumber;

    @NotNull
    private String password;

    @Email(message = "Valid email expected")
    @Column(unique = true)
    private String email;

    private String address;

    private Boolean enabled = true;

    @Transient
    @JsonIgnore
    private Set<String> roles = new HashSet<>();

    @Override
    @JsonDeserialize(using = GrantedAuthorityDeserializer.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        roles.add("USER");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return null == email ? phoneNumber : email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
