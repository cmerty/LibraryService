package com.example.demo.service.Interfaces;

import com.example.demo.model.DTO.Author.AuthorDto;
import com.example.demo.model.DTO.Author.AuthorDtoWithAuthorID;
import com.example.demo.model.DTO.Author.AuthorDtoWithBook;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.ID.IDDto;

import java.util.List;

public interface AuthorServiceInterface {

    AuthorDtoWithAuthorID createNewAuthor(AuthorDto authorDto);

    List<AuthorDtoWithBook> addBookToAuthor(Long id, BookDto bookDto);

    List<BookDtoWithBookID> getBooksByAuthor(Long id);

    List<BookDto> deleteBookByAuthor(Long authorId, Long bookId);

    List<AuthorDtoWithAuthorID> getAllAuthors();

    IDDto changeAuthorsName(Long id, AuthorDto authorDto);

    void deleteAuthor(Long id);

    AuthorDtoWithAuthorID getAuthorById(Long id);

}

