package com.example.demo.service.Convertors;

import com.example.demo.model.Book;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import com.example.demo.service.Convertors.MapStructInterfaces.BookConverter;
import com.example.demo.service.Convertors.MapStructInterfaces.BookDtoWithAuthorIdConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookConverterImpl {

    public BookDto convertBookToBookDto(Book book) {
        return BookConverter.INSTANCE.convertBookToBookDto(book);
    }

    public Book convertBookDtoToBook(BookDto bookDto) {
        return BookConverter.INSTANCE.convertBookDtoToBook(bookDto);
    }

    public BookDtoWithAuthorID convertBookToBookDtoWithAuthorId(Book book) {
        return BookConverter.INSTANCE.convertBookToBookDtoWithAuthorId(book);
    }

    public Book convertBookDtoWithAuthorIdToBook(BookDtoWithAuthorID bookDtoWithAuthorID) {
        return BookConverter.INSTANCE.convertBookDtoWithAuthorIdToBook(bookDtoWithAuthorID);
    }

}
