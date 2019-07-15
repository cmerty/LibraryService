package com.example.demo.service.DBService;

import com.example.demo.service.Interfaces.AuthorServiceInterface;
import com.example.demo.utility.exception.NoContentException;
import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.DTO.Author.AuthorDto;
import com.example.demo.model.DTO.Author.AuthorDtoWithAuthorID;
import com.example.demo.model.DTO.Author.AuthorDtoWithBook;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.Book.BookDtoWithBookID;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BooksRepository;
import com.example.demo.service.Convertors.EntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Profile("Db")
@RequiredArgsConstructor
public class AuthorService implements AuthorServiceInterface {

    private final AuthorRepository authorRepository;

    private final BooksRepository booksRepository;

    private final EntityConverter<Author, AuthorDto> authorConverter;

    private final EntityConverter<Book, BookDto> bookConverter;

    @Transactional
    @Override
    public AuthorDtoWithAuthorID createNewAuthor(AuthorDto authorDto) {
        Author author = authorConverter.convertRevers(authorDto);
        author = authorRepository.save(author);
        return new AuthorDtoWithAuthorID(author.getId(), author.getFullName());
    }

    @Transactional
    @Override
    public List<AuthorDtoWithBook> addBookToAuthor(Long id, BookDto bookDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        Book book = bookConverter.convertRevers(bookDto);
        author.addBook(book);
        author = authorRepository.save(author);
        Optional<List<Book>> allByAuthor = booksRepository.findAllByAuthor(author);
        return allByAuthor.orElseThrow(()->new NotFoundException("List of books has not found")).stream()
                .map(expectedBook -> new AuthorDtoWithBook(expectedBook.getId(), expectedBook.getName(), expectedBook.getDescription()))
                .collect(toList());
    }

    @Transactional
    @Override
    public List<AuthorDtoWithAuthorID> getAllAuthors() {
        List<Author> authorsInLibrary = authorRepository.findAll();
        return authorsInLibrary.stream()
                .map(expectedAuthors -> new AuthorDtoWithAuthorID(expectedAuthors.getId(), expectedAuthors.getFullName()))
                .collect(toList());
    }

    @Transactional
    @Override
    public IDDto changeAuthorsName(Long id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Author not found"));
        author.setFullName(authorDto.getFullName());
        author = authorRepository.save(author);
        return new IDDto(author.getId());
    }

    @Transactional
    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Author not found"));
        authorRepository.delete(author);
    }

    @Override
    public AuthorDtoWithAuthorID getAuthorById(Long id) {
        Author author = authorRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Author not found"));
        return new AuthorDtoWithAuthorID(id, author.getFullName());
    }

    @Transactional
    @Override
    public List<BookDtoWithBookID> getBooksByAuthor(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author not found"));
        return author.getBooksList().stream()
                .map(expectedBook -> new BookDtoWithBookID(expectedBook.getName(), expectedBook.getId(), expectedBook.getDescription()))
                .collect(toList());
    }

    @Transactional
    @Override
    public List<BookDto> deleteBookByAuthor(Long authorId, Long bookId) {
        Author author = authorRepository.findById(authorId).
                orElseThrow(() -> new NotFoundException("Author not found"));
        Book book = booksRepository.findById(bookId).
                orElseThrow(() -> new NoContentException("Book not found"));
        if (author.getBooksList().contains(book)) {
            author.getBooksList().remove(book);
            booksRepository.delete(book);
            authorRepository.save(author);
        }
        return author.getBooksList().stream()
                .map(bookConverter::convert)
                .collect(toList());
    }

}
