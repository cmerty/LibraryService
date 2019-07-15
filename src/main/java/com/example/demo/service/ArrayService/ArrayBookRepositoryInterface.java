package com.example.demo.service.ArrayService;

import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Book.BookDtoWithIdAndCategories;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.ID.ListOfIDDto;
import com.example.demo.service.Convertors.EntityConverter;
import com.example.demo.service.Interfaces.BookServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Profile("ArrayBook")
@Repository
public class ArrayBookRepositoryInterface implements BookServiceInterface {

    private final EntityConverter<Book, BookDto> bookConverter;

    private final EntityConverter<Book, BookDtoWithAuthorID> bookBookDtoWithAuthorIDEntityConverter;

    private ArrayList<Book> books = new ArrayList<>();

    private ArrayList<Category> categories = new ArrayList<>();

    @Override
    public List<BookDtoWithBookID> getAllBooks() {
        ArrayList<BookDtoWithBookID> bookDtos = new ArrayList<>();
        books.forEach(book -> bookDtos.add(new BookDtoWithBookID(book.getName(), book.getId(), book.getDescription())));
        return bookDtos;
    }

    @Override
    public BookDtoWithBookID getBookById(Long id) {
        Book expectedBook = books.stream().
                filter(book -> book.getId() == id).
                findFirst().orElseThrow(() -> new NotFoundException("Book not found"));
        return new BookDtoWithBookID(expectedBook.getName(), expectedBook.getId(), expectedBook.getDescription());
    }

    @Override
    public List<BookDto> deleteBookById(Long id) {
        books.removeIf(book -> book.getId() == id);
        ArrayList<BookDto> bookDtos = new ArrayList<>();
        books.forEach(book -> bookDtos.add(bookConverter.convert(book)));
        return bookDtos;
    }

    @Override
    public BookDtoWithIdAndCategories deleteCategoriesInBook(Long id, ListOfIDDto idOfCategories) {
        return null;
    }

    @Override
    public BookDtoWithBookID changeBook(Long id, BookDto bookDto) {
        BookDtoWithBookID bookDtoWithBookId = new BookDtoWithBookID();
        books.stream().
                filter(book -> book.getId() == id)
                .findFirst()
                .ifPresent(book -> {
                    book.setName(bookDto.getName());
                    book.setDescription(bookDto.getDescription());
                    bookDtoWithBookId.setId(book.getId());
                    bookDtoWithBookId.setName(book.getName());
                    bookDtoWithBookId.setDescription(book.getDescription());
                });
        return bookDtoWithBookId;
    }

    @Override
    public IDDto addBook(BookDtoWithAuthorID bookDtoWithAuthorID) {
        Book book = bookBookDtoWithAuthorIDEntityConverter.convertRevers(bookDtoWithAuthorID);
        books.add(book);
        return new IDDto(book.getId());
    }

    @Override
    public BookDtoWithIdAndCategories addCategoriesToBook(Long id, ListOfIDDto idOfCategories) {
        return null;
    }

}


