package com.example.demo.service.Convertors;

import com.example.demo.model.Book;
import com.example.demo.model.DTO.Book.BookDto;
import org.springframework.stereotype.Component;

@Component
public class BookConverter implements EntityConverter<Book, BookDto> {

    @Override
    public BookDto convert(Book book) {
        return new BookDto(book.getName(), book.getDescription());
    }

    @Override
    public Book convertRevers(BookDto bookDto) {
        return new Book(bookDto.getName(), bookDto.getDescription());
    }

}
