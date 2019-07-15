package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Entity
@Table(name = "Authors")
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    private String fullName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Book> booksList;

    public Author(@NotBlank String fullName) {
        this.fullName = fullName;
    }

    public void addBook(Book book) {
        book.setAuthor(this);
        if (isNull(booksList)) {
            booksList = new ArrayList<>();
        }
        booksList.add(book);
    }

}

