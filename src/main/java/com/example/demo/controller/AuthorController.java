package com.example.demo.controller;

import com.example.demo.model.DTO.Author.AuthorDto;
import com.example.demo.model.DTO.Author.AuthorDtoWithAuthorID;
import com.example.demo.model.DTO.Author.AuthorDtoWithBook;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.service.Interfaces.AuthorServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorServiceInterface authorServiceInterface;

    public AuthorController(AuthorServiceInterface authorServiceInterface) {
        this.authorServiceInterface = authorServiceInterface;
    }

    @GetMapping
    public List<AuthorDtoWithAuthorID> allAuthors() {
        return authorServiceInterface.getAllAuthors();
    }

    @GetMapping("/{id}")
    public AuthorDtoWithAuthorID findAuthorById(@PathVariable Long id) {
        return authorServiceInterface.getAuthorById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookDtoWithBookID> allBooksByAuthor(@PathVariable Long id) {

        return authorServiceInterface.getBooksByAuthor(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public AuthorDtoWithAuthorID createNewAuthor(@Valid @RequestBody AuthorDto authorDto) {

        return authorServiceInterface.createNewAuthor(authorDto);
    }

    @PutMapping(value = "/{id}")
    public IDDto changeAuthorsData(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        return authorServiceInterface.changeAuthorsName(id, authorDto);
    }

    @PutMapping("/{id}/addBook")
    public List<AuthorDtoWithBook> addBookToAuthor(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        return authorServiceInterface.addBookToAuthor(id, bookDto);
    }

    @DeleteMapping("/{id}")
    public void deleteAuthors(@PathVariable Long id) {
        authorServiceInterface.deleteAuthor(id);
    }

    @DeleteMapping("{authorId}/book/{bookId}")
    public ResponseEntity<String> deleteBook(@Valid @PathVariable Long authorId, @Valid @PathVariable Long bookId) {
        authorServiceInterface.deleteBookByAuthor(authorId, bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Book was deleted");
    }

}
