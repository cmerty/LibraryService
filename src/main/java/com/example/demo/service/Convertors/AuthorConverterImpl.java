package com.example.demo.service.Convertors;

import com.example.demo.model.Author;
import com.example.demo.model.DTO.Author.AuthorDto;
import com.example.demo.service.Convertors.MapStructInterfaces.AuthorConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorConverterImpl {


    public AuthorDto convert(Author author) {
       return AuthorConverter.INSTANCE.convertAuthorToAuthorDto(author);
    }

    public Author convertRevers(AuthorDto authorDto) {
       return AuthorConverter.INSTANCE.convertAuthorDtoToAuthor(authorDto);
    }


}
