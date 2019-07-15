package com.example.demo.model.DTO.Author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDtoWithAuthorID {

    @NotNull
    private Long id;
    @NotBlank
    private String fullName;

}


