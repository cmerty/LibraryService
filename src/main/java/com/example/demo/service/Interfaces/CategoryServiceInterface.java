package com.example.demo.service.Interfaces;

import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Category.CategoryDto;
import com.example.demo.model.DTO.Category.CategoryDtoWithCategoryId;
import com.example.demo.model.DTO.ID.IDDto;

import java.util.List;

public interface CategoryServiceInterface {

    IDDto addCategory(CategoryDto categoryDto);

    CategoryDtoWithCategoryId getCategoryById(Long id);

    List<CategoryDtoWithCategoryId> getAllCategories();

    List<BookDtoWithBookID> getAllBooksWithCategoryId(Long id);

    List<CategoryDto> deleteCategoryById(Long id);

}
