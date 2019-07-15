package com.example.demo.controller;

import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Book.BookDtoWithIdAndCategories;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.ID.ListOfIDDto;
import com.example.demo.service.Interfaces.BookServiceInterface;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/book")
public class BookController {

    private final BookServiceInterface bookRepository;

    public BookController(BookServiceInterface bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<BookDtoWithBookID> findAllBook() {
        return bookRepository.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDtoWithBookID findBook(@PathVariable Long id) {
        return bookRepository.getBookById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IDDto createNewBook(@Valid @RequestBody BookDtoWithAuthorID bookDtoWithAuthorID) {
        return bookRepository.addBook(bookDtoWithAuthorID);
    }

    @PostMapping("/{id}/category")
    public BookDtoWithIdAndCategories addCategories(@PathVariable Long id, @Valid @RequestBody ListOfIDDto idOfCategories) {
        return bookRepository.addCategoriesToBook(id, idOfCategories);
    }

    @PutMapping("/{id}")
    public BookDtoWithBookID changeBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        return bookRepository.changeBook(id, bookDto);
    }

    @DeleteMapping("/{id}")
    public List<BookDto> deleteBook(@PathVariable Long id) {
        return bookRepository.deleteBookById(id);
    }

    @DeleteMapping("/{id}/category")
    public BookDtoWithIdAndCategories deleteBookWithCategory(@PathVariable Long id, @Valid @RequestBody ListOfIDDto idOfCategories) {
        return bookRepository.deleteCategoriesInBook(id, idOfCategories);
    }

}
