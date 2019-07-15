package com.example.demo.model.DTO.Author;

import com.example.demo.model.DTO.Book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @NotBlank
    private String fullName;
    private List<BookDto> booksList;

    public AuthorDto(@NotBlank String fullName) {
        this.fullName = fullName;
    }

}
