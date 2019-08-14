package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Entity
@Table(name = "Book")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull
    @ManyToOne
    private Author author;

    @Column(unique = true)
    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "book")
    private List<Category> categories;

    public Book(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addCategory(Category category) {
        if (isNull(category.getBook())) {
            List<Book> bookList = new ArrayList<>();
            category.setBook(bookList);
        }
        category.getBook().add(this);

        if (isNull(categories))
            categories = new ArrayList<>();

        categories.add(category);
    }

    public void addCategories(List<Category> newCategoriesList) {
        if (isNull(categories))
            categories = new ArrayList<>();

        newCategoriesList.forEach(this::addCategoryIfNotContains);
    }

    private void addCategoryIfNotContains(Category category) {
        if (isNull(category.getBook()) || !this.categories.contains(category)) {
            List<Book> bookList = new ArrayList<>();
            category.setBook(bookList);
            category.getBook().add(this);
            this.categories.add(category);
        }

    }

}

