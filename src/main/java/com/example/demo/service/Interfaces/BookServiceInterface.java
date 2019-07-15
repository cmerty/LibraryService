package com.example.demo.service.Interfaces;

import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Book.BookDtoWithIdAndCategories;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.ID.ListOfIDDto;

import java.util.List;

public interface BookServiceInterface {

    List<BookDtoWithBookID> getAllBooks();

    BookDtoWithBookID getBookById(Long id);

    IDDto addBook(BookDtoWithAuthorID bookDtoWithAuthorID);

    BookDtoWithIdAndCategories addCategoriesToBook(Long id, ListOfIDDto idOfCategories);

    BookDtoWithBookID changeBook(Long id, BookDto bookDto);

    List<BookDto> deleteBookById(Long id);

    BookDtoWithIdAndCategories deleteCategoriesInBook(Long id, ListOfIDDto idOfCategories);

}
