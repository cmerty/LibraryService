package com.example.demo.model.DTO.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDtoWithBookID {

    @NotBlank
    private String name;
    @NotNull
    private Long id;
    @NotBlank
    private String description;

}
