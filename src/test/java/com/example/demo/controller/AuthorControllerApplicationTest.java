package com.example.demo.controller;

import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Book;
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
public class AuthorControllerApplicationTest {

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

    @Before
    public void setUp() {
        accountRepository.deleteAll();
        booksRepository.deleteAll();
        authorRepository.deleteAll();
        categoriesRepository.deleteAll();
        accountRepository.deleteAll();
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
    public void inValidateAuthorRequest_1() throws Exception {
        server.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidCreateAuthorRequest_1()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void inValidateAuthorRequest_2() throws Exception {

        server.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidCreateAuthorRequest_2()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void validateAuthorRequest() throws Exception {
        assertThat(authorRepository.findAll().size(), is(0));
        server.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validCreateAuthorRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(authorRepository.findAll().get(0).getFullName(), is("Стивен Кинг"));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void updateAuthor() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(put("/authors/" + authorId)
                .content(validCreateAuthorRequest())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(authorRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Author has not found")).getFullName(), is("Стивен Кинг"));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void updateAuthorValidException_1() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(put("/authors/" + authorId)
                .content(inValidCreateAuthorRequest_1())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void updateAuthorValidException_2() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(put("/authors/" + authorId)
                .content(inValidCreateAuthorRequest_2())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteAuthor() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(delete("/authors/" + authorId))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(0));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteAuthorWIthBook() throws Exception {
        long authorId = getAuthorWithBookFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        server.perform(delete("/authors/" + authorId))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(0));
        assertThat(booksRepository.findAll().size(), is(0));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteNotExistAuthor() throws Exception {
        getAuthorFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(delete("/authors/" + 1000))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertThat(authorRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findByIdNotExistAuthor() throws Exception {
        getAuthorFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(get("/authors/" + 5555))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findById() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(get("/authors/" + authorId))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.fullName").value("Антон Гепало"))
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAll() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        server.perform(get("/authors"))
                .andDo(print())
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].fullName").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(authorId))
                .andExpect(jsonPath("$[0].fullName").value("Антон Гепало"))
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
    }


    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void addBookAuthorInvalidException() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(put("/authors/" + authorId + "/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidAddBookRequest_1()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        server.perform(put("/authors/" + authorId + "/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidAddBookRequest_2()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        server.perform(put("/authors/" + authorId + "/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidAddBookRequest_3()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void addBookAuthor() throws Exception {
        long authorId = getAuthorWithBookFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        server.perform(put("/authors/" + authorId + "/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddBookRequest()))
                .andDo(print())
                .andExpect(jsonPath("$[*]", hasSize(3)))
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(3));

    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void addBookAuthorAndAuthorNotExist() throws Exception {
        getAuthorFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(put("/authors/" + 1212121 + "/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddBookRequest()))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllAuthorBookAndAuthorNotExist() throws Exception {
        getAuthorFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(get("/authors/" + 1212121 + "/books"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllAuthorEmptyList() throws Exception {
        long authorId = getAuthorFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(get("/authors/" + authorId + "/books"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllAuthorBook() throws Exception {
        long authorId = getAuthorWithBookFromDb().getId();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        server.perform(get("/authors/" + authorId + "/books"))
                .andDo(print())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(jsonPath("$[*].name").isNotEmpty())
                .andExpect(jsonPath("$[*].description").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteAuthorBook() throws Exception {
        getAuthorWithBookFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        Book book = booksRepository.findAll().get(0);
        String requestAPI = String.format("/authors/%s/book/%s", book.getAuthor().getId(), book.getId());
        server.perform(delete(requestAPI))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteAuthorBookAuthorNotFound() throws Exception {
        getAuthorWithBookFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        String requestAPI = String.format("/authors/%s/book/%s", 10, 10);
        server.perform(delete(requestAPI))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Author not found"))
                .andExpect(status().isNotFound());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteAuthorBookAndBookNotFound() throws Exception {
        getAuthorWithBookFromDb();
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        Long authorId = authorRepository.findAll().get(0).getId();
        String requestAPI = String.format("/authors/%s/book/%s", authorId, 10);
        server.perform(delete(requestAPI))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Book not found"))
                .andExpect(status().isNoContent());
        assertThat(authorRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
    }

    private Author getAuthorWithBookFromDb() {
        Author author = new Author();
        author.setFullName("Антон Гепало");
        Book book = new Book();
        book.setName("Как вийти з запою");
        book.setDescription("Ніяк");
        author.addBook(book);

        Book book_2 = new Book();
        book_2.setName("Java");
        book_2.setDescription("Про Java");
        author.addBook(book_2);
        return authorRepository.save(author);
    }

    private Author getAuthorFromDb() {
        Author author = new Author();
        author.setFullName("Антон Гепало");
        return authorRepository.save(author);
    }


    private String validAddBookRequest() {
        return "{" +
                " \"name\" : \"Книга Стаса\"," +
                " \"description\" : \"Становлення Стаса ч.1\"" +
                "}";
    }

    private String inValidAddBookRequest_1() {
        return "{" +
                " \"name\" : \"\"," +
                " \"description\" : \"Становлення Стаса ч.1\"" +
                "}";
    }

    private String inValidAddBookRequest_2() {
        return "{" +
                " \"name\" : \"Книга Стаса\"," +
                " \"description\" : \"\"" +
                "}";
    }

    private String inValidAddBookRequest_3() {
        return "{" +
                " \"name\" : \"\"," +
                " \"description\" : \"\"" +
                "}";
    }


    private String validCreateAuthorRequest() {
        return "{" +
                " \"fullName\" : \"Стивен Кинг\"" +
                "}";
    }

    private String inValidCreateAuthorRequest_1() {
        return "{" +
                " \"fullName\" : \"\"" +
                "}";
    }

    private String inValidCreateAuthorRequest_2() {
        return "{" +
                " \"fullName\" : null" +
                "}";
    }

}