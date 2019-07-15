package com.example.demo.model.DTO.Book;

import com.example.demo.model.DTO.Category.CategoryDtoOnlyWithName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDtoWithIdAndCategories {

    @NotBlank
    private String name;
    @NotNull
    private Long id;
    @NotBlank
    private String description;
    private List<CategoryDtoOnlyWithName> categories;

}
