package com.example.demo.controller;

import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Category.CategoryDto;
import com.example.demo.model.DTO.Category.CategoryDtoWithCategoryId;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.service.Interfaces.CategoryServiceInterface;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryServiceInterface categoryRepository;

    public CategoryController(CategoryServiceInterface categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryDtoWithCategoryId> findAllCategories() {
        return categoryRepository.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryDtoWithCategoryId findCategory(@PathVariable Long id) {
        return categoryRepository.getCategoryById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookDtoWithBookID> getAllBooksWithCategoryId(@PathVariable Long id) {
        return categoryRepository.getAllBooksWithCategoryId(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IDDto createNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryRepository.addCategory(categoryDto);
    }

    @DeleteMapping("/{id}")
    public List<CategoryDto> deleteCategory(@PathVariable Long id) {
        return categoryRepository.deleteCategoryById(id);
    }

}
