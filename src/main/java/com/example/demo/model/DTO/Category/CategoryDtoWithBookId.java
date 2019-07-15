package com.example.demo.model.DTO.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDtoWithBookId {

    @NotBlank
    private String nameOfCategory;
    @NotNull
    private Long id;

}
