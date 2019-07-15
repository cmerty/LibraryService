package com.example.demo.service.Convertors;

import com.example.demo.model.Author;
import com.example.demo.model.DTO.Author.AuthorDto;
import org.springframework.stereotype.Component;

@Component
public class AuthorConverter implements EntityConverter<Author, AuthorDto> {

    @Override
    public AuthorDto convert(Author author) {
        return new AuthorDto(author.getFullName());
    }

    @Override
    public Author convertRevers(AuthorDto authorDto) {
        return new Author(authorDto.getFullName());
    }

}
