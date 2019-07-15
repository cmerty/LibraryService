package com.example.demo.model.DTO.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    @NotBlank
    private String name;
    @NotBlank
    private String description;

}
