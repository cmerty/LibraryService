package com.example.demo.repository;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BooksRepository extends JpaRepository<Book, Long> {

    @Query(value = "select book " +
            "from Book book " +
            "left join fetch book.author author " +
            "where book.author.id=author.id"
    )
    Optional<List<Book>> findAllByAuthor(Author author);

    Optional<List<Book>> findAllByCategories(Category category);
}
