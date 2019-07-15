package com.example.demo.service.DBService;

import com.example.demo.model.DTO.Category.CategoryDtoOnlyWithName;
import com.example.demo.service.Interfaces.BookServiceInterface;
import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithAuthorID;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.Book.BookDtoWithIdAndCategories;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.ID.ListOfIDDto;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BooksRepository;
import com.example.demo.repository.CategoriesRepository;
import com.example.demo.service.Convertors.EntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Profile("Db")
public class BookService implements BookServiceInterface {

    private final AuthorRepository authorRepository;

    private final BooksRepository booksRepository;

    private final CategoriesRepository categoriesRepository;

    private final EntityConverter<Book, BookDto> bookConverter;

    private final EntityConverter<Book, BookDtoWithAuthorID> bookBookDtoWithAuthorIDEntityConverter;

    @Override
    @Transactional
    public List<BookDtoWithBookID> getAllBooks() {
        return booksRepository.findAll().stream()
                .map(expectedBook -> new BookDtoWithBookID(expectedBook.getName(), expectedBook.getId(), expectedBook.getDescription()))
                .collect(toList());

    }

    @Override
    @Transactional
    public BookDtoWithBookID getBookById(Long id) {
        Book book = booksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return new BookDtoWithBookID(book.getName(), book.getId(), book.getDescription());

    }

    @Override
    @Transactional
    public IDDto addBook(BookDtoWithAuthorID bookDtoWithAuthorID) {
        Author author = authorRepository.
                findById(bookDtoWithAuthorID.getAuthorId()).
                orElseThrow(() -> new NotFoundException("Author not found"));

        Book book = bookBookDtoWithAuthorIDEntityConverter.convertRevers(bookDtoWithAuthorID);

        book.setAuthor(author);
        book = booksRepository.save(book);
        return new IDDto(book.getId());

    }

    @Override
    @Transactional
    public BookDtoWithIdAndCategories addCategoriesToBook(Long id, ListOfIDDto categories) {
        Book book = booksRepository.
                findById(id).orElseThrow(() -> new NotFoundException("Book not found"));

        List<Long> listOfNewCategoriesId = categories
                .getListOfIDDto()
                .stream()
                .map(IDDto::getId)
                .collect(toList());

        List<Category> newCategoriesList = categoriesRepository.findAllById(listOfNewCategoriesId);

        book.addCategories(newCategoriesList);


        book = booksRepository.save(book);

        return new BookDtoWithIdAndCategories(book.getName(), book.getId(), book.getDescription(), new ArrayList<>(book.getCategories().stream().map(category -> new CategoryDtoOnlyWithName(category.getNameOfCategory())).collect(toList())));
    }

    @Override
    @Transactional
    public BookDtoWithBookID changeBook(Long id, BookDto bookDto) {
        Book bookFromLibrary;

        bookFromLibrary = booksRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Book not found"));
        bookFromLibrary.setName(bookDto.getName());
        bookFromLibrary.setDescription(bookDto.getDescription());

        booksRepository.save(bookFromLibrary);

        return new BookDtoWithBookID(bookFromLibrary.getName(), bookFromLibrary.getId(), bookFromLibrary.getDescription());
    }

    @Override
    @Transactional
    public List<BookDto> deleteBookById(Long id) {

        booksRepository.deleteById(id);

        List<Book> book = booksRepository.findAll();
        return book.stream().
                map(bookConverter::convert)
                .collect(toList());
    }

    @Override
    @Transactional
    public BookDtoWithIdAndCategories deleteCategoriesInBook(Long id, ListOfIDDto categories) {
        Book book = booksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<Long> listOfNewCategoriesId = categories
                .getListOfIDDto()
                .stream()
                .map(IDDto::getId)
                .collect(toList());

        Optional<List<Category>> removeCategoriesList = categoriesRepository.findAllByIdIn(listOfNewCategoriesId);

        book.getCategories().removeAll(removeCategoriesList.orElseThrow(() -> new NotFoundException("Categories has not found")));

        booksRepository.save(book);

        return new BookDtoWithIdAndCategories(book.getName(), book.getId(), book.getDescription(), book.getCategories().stream().map(category -> new CategoryDtoOnlyWithName(category.getNameOfCategory())).collect(toList()));
    }

}