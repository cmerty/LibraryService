package com.example.demo.service.Convertors.MapStructInterfaces;

import com.example.demo.model.Author;
import com.example.demo.model.DTO.Author.AuthorDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorConverter {

    AuthorConverter INSTANCE = Mappers.getMapper( AuthorConverter.class );

    AuthorDto convertAuthorToAuthorDto(Author author);

    Author convertAuthorDtoToAuthor(AuthorDto authorDto);
}
