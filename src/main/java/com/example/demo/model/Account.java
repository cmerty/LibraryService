package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Role role;
    @ManyToMany
    private List<Book> readBooks;

    public Account() {
        readBooks = new ArrayList<>();
    }

    public Account(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.readBooks = new ArrayList<>();
    }

    public void addBook(Book book) {
        if (isNull(readBooks)) {
            readBooks = new ArrayList<>();
        }
        readBooks.add(book);
    }

    public void removeBook(Book book) {
        readBooks.remove(book);
    }

}
