package com.example.demo.controller;

import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("Db")
@AutoConfigureMockMvc
public class BookControllerApplicationTest {

    @Autowired
    private MockMvc server;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Category category;

    private Category secondCategory;

    @Before
    public void setUp() {
        accountRepository.deleteAll();
        booksRepository.deleteAll();
        authorRepository.deleteAll();
        categoriesRepository.deleteAll();
        accountRepository.deleteAll();
        category = null;
        secondCategory = null;
    }

    @Test
    public void testValue() {
        assertNotNull(accountRepository);
        assertNotNull(booksRepository);
        assertNotNull(authorRepository);
        assertNotNull(categoriesRepository);
        assertNotNull(roleRepository);
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void createBookInvalidRequest() throws Exception {
        long id = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        String request = String.format(createBookRequestInvalid_1(), id);
        server.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        request = String.format(createBookRequestInvalid_2(), id);
        server.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        request = String.format(createBookRequestInvalid_3(), null);
        server.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void createBookAuthorNotFoundException() throws Exception {
        getAuthorFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        String request = String.format(createBookRequest(), 525);
        server.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void createBookAuthor() throws Exception {
        long id = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        String request = String.format(createBookRequest(), id);
        server.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(1));

        Book book = booksRepository.findAll().get(0);
        assertThat(book.getName(), is("Книга Стаса"));
        assertThat(book.getDescription(), is("Становлення Стаса ч.1"));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void getBookWithException() throws Exception {
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(get("/book/52"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void getBook() throws Exception {
        long bookId = getBookFromDb().getId();
        assertThat(booksRepository.findAll().size(), is(1));
        server.perform(get("/book/" + bookId))
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.name").value("Как вийти з запою"))
                .andExpect(jsonPath("$.description").value("Ніяк"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void getAllBook() throws Exception {
        long bookId = getBookFromDb().getId();
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(get("/book"))
                .andExpect(jsonPath("$[*]", hasSize(1)))
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(bookId))
                .andExpect(jsonPath("$[0].name").value("Как вийти з запою"))
                .andExpect(jsonPath("$[0].description").value("Ніяк"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteBook() throws Exception {
        long bookId = getBookFromDb().getId();
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));

        server.perform(delete("/book/" + bookId))
                .andExpect(status().isOk());
        assertThat(booksRepository.findAll().size(), is(0));
        assertThat(authorRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void updateBook() throws Exception {
        long bookId = getBookFromDb().getId();
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));

        server.perform(put("/book/" + bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(updateBookRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.name").value("Книга"))
                .andExpect(jsonPath("$.description").value("Опис"))
                .andExpect(status().isOk());

        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        Book book = booksRepository.findById(bookId).orElseThrow(()->new NotFoundException("Book has not found"));
        assertThat(book.getName(), is("Книга"));
        assertThat(book.getDescription(), is("Опис"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void addCategoryInBook() throws Exception {
        long bookId = getBookFromDb().getId();
        getCategoryFromDb();
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(categoriesRepository.findAll().size(), is(2));

        server.perform(post("/book/" + bookId + "/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(addCategoriesRequest(this.secondCategory.getId())))
                .andDo(print())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.categories").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findById(bookId).orElseThrow(()->new NotFoundException("Book has not found")).getCategories().size(), is(2));
    }

    @Transactional
    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteCategoryInBook() throws Exception {
        long bookId = getBookFromDb().getId();
        getCategoryFromDb();
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(categoriesRepository.findAll().size(), is(2));

        String content = deleteCategoryInBookRequestWithParameter(this.category.getId(), this.secondCategory.getId());
        server.perform(delete("/book/" + bookId + "/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.name").value("Как вийти з запою"))
                .andExpect(jsonPath("$.description").value("Ніяк"))
                .andExpect(jsonPath("$.categories").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(booksRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findById(bookId).orElseThrow(()->new NotFoundException("Book has not found")).getCategories().size(), is(0));
    }

    private Book getBookFromDb() {
        Author author = new Author();
        author.setFullName("Антон Гепало");
        author = authorRepository.save(author);

        Book book = new Book();
        book.setName("Как вийти з запою");
        book.setDescription("Ніяк");
        book.setAuthor(author);

        this.category = new Category();
        category.setNameOfCategory("Комедия");
        category = categoriesRepository.save(category);
        book.addCategory(category);

        return booksRepository.save(book);
    }

    private Author getAuthorFromDb() {
        Author author = new Author();

        author.setFullName("Антон Гепало");

        return authorRepository.save(author);
    }

    private Category getCategoryFromDb() {

        this.secondCategory = new Category();

        secondCategory.setNameOfCategory("Ужасы");

        return categoriesRepository.save(secondCategory);
    }

    private String addCategoriesRequest(Long id_1) {
        return String.format("{"
                + " \"listOfIDDto\" : [{\"id\": \"%s\"},{\"id\": \"%s\"}]  " + "}", id_1, 20);
    }

    private String deleteCategoryInBookRequestWithParameter(Long id_1, Long id_2) {
        return String.format("{"
                + " \"listOfIDDto\" : [{\"id\": \"%s\"},{\"id\": \"%s\"}]  " + "}", id_1, id_2);
    }

    private String createBookRequest() {
        return "{" +
                " \"name\" : \"Книга Стаса\"," +
                " \"authorId\" : \"%s\"," +
                " \"description\" : \"Становлення Стаса ч.1\"" +
                "}";
    }

    private String updateBookRequest() {
        return "{" +
                " \"name\" : \"Книга\"," +
                " \"description\" : \"Опис\"" +
                "}";
    }

    private String createBookRequestInvalid_1() {
        return "{" +
                " \"name\" : \"\"," +
                " \"authorId\" : \"%s\"," +
                " \"description\" : \"Становлення Стаса ч.1\"" +
                "}";
    }

    private String createBookRequestInvalid_2() {
        return "{" +
                " \"name\" : \"Книга Стаса\"," +
                " \"authorId\" : \"%s\"," +
                " \"description\" : \"\"" +
                "}";
    }

    private String createBookRequestInvalid_3() {
        return "{" +
                " \"name\" : \"Книга Стаса\"," +
                " \"authorId\" : \"%s\"," +
                " \"description\" : \"Становлення\"" +
                "}";
    }

}
