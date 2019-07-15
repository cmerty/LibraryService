package com.example.demo.service.DBService;

import com.example.demo.utility.exception.ConflictException;
import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Category.CategoryDto;
import com.example.demo.model.DTO.Category.CategoryDtoWithCategoryId;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.repository.BooksRepository;
import com.example.demo.repository.CategoriesRepository;
import com.example.demo.service.Interfaces.CategoryServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Profile("Db")
public class CategoriesService implements CategoryServiceInterface {

    private final CategoriesRepository categoriesRepository;

    private final BooksRepository booksRepository;

    @Override
    public IDDto addCategory(CategoryDto categoryDto) {
        if (categoriesRepository.findByNameOfCategory(categoryDto.getNameOfCategory()).isPresent()) {
            throw new ConflictException("Category with name has already exist");
        }

        Category category = new Category(categoryDto.getNameOfCategory());

        category = categoriesRepository.save(category);

        return new IDDto(category.getId());
    }

    @Override
    public CategoryDtoWithCategoryId getCategoryById(Long id) {
        Category category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return new CategoryDtoWithCategoryId(category.getId(), category.getNameOfCategory());
    }

    @Override
    public List<CategoryDtoWithCategoryId> getAllCategories() {
        return categoriesRepository.findAll()
                .stream()
                .map(expectedCategories -> new CategoryDtoWithCategoryId(expectedCategories.getId(), expectedCategories.getNameOfCategory()))
                .collect(toList());
    }

    @Override
    public List<BookDtoWithBookID> getAllBooksWithCategoryId(Long id) {
        Category category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return category.getBook()
                .stream()
                .map(expectedBook -> (new BookDtoWithBookID(expectedBook.getName(), expectedBook.getId(), expectedBook.getDescription())))
                .collect(toList());
    }

    @Override
    public List<CategoryDto> deleteCategoryById(Long id) {
        Category category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category has not found"));

        if (booksRepository.findAllByCategories(category).isPresent()) {

            List<Book> bookWithExpectedCategory = booksRepository.findAllByCategories(category)
                    .orElseThrow(() -> new NotFoundException("Books with expected category has not found"));

            bookWithExpectedCategory.forEach(book -> book.getCategories().remove(category));
            booksRepository.saveAll(bookWithExpectedCategory);
        }

        categoriesRepository.deleteById(id);

        return categoriesRepository.findAll()
                .stream()
                .map(expectedCategories -> new CategoryDto(expectedCategories.getNameOfCategory(),
                        expectedCategories.getBook()
                                .stream()
                                .map(expectedBook -> new BookDto(expectedBook.getName(), expectedBook.getDescription()))
                                .collect(toList()))
                )
                .collect(toList());
    }

}
