package com.example.demo.service.Convertors.MapStructInterfaces;

import com.example.demo.model.Book;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookDtoWithAuthorIdConverter {

    BookDtoWithAuthorIdConverter INSTANCE= Mappers.getMapper(BookDtoWithAuthorIdConverter.class);



}


