package com.example.demo.service.Convertors.MapStructInterfaces;

import com.example.demo.model.Book;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookConverter {

    BookConverter INSTANCE = Mappers.getMapper(BookConverter.class);

    BookDto convertBookToBookDto(Book book);

    Book convertBookDtoToBook(BookDto bookDto);

    @Mapping(target ="authorId",source = "author.id")
    BookDtoWithAuthorID convertBookToBookDtoWithAuthorId (Book book);

    Book convertBookDtoWithAuthorIdToBook(BookDtoWithAuthorID bookDtoWithAuthorID);


}
