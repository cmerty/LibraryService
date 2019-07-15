package com.example.demo.service.Convertors;

import com.example.demo.model.Book;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import org.springframework.stereotype.Component;

@Component
public class BookDtoWithAuthorIdConverter implements EntityConverter<Book, BookDtoWithAuthorID> {

    @Override
    public BookDtoWithAuthorID convert(Book book) {
        return new BookDtoWithAuthorID(book.getName(), book.getId(), book.getDescription());
    }

    @Override
    public Book convertRevers(BookDtoWithAuthorID bookDtoWithAuthorID) {
        return new Book(bookDtoWithAuthorID.getName(), bookDtoWithAuthorID.getDescription());
    }

}
