package com.example.demo.model.DTO.Category;

import com.example.demo.model.DTO.Book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotBlank
    private String nameOfCategory;
    private List<BookDto> book;

}
