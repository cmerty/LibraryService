package com.example.demo.controller;

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

import java.util.ArrayList;

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
public class CategoryControllerApplicationTest {

    @Autowired
    private MockMvc server;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        booksRepository.deleteAll();
        authorRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void testValue() {
        assertNotNull(accountRepository);
        assertNotNull(booksRepository);
        assertNotNull(authorRepository);
        assertNotNull(categoryRepository);
        assertNotNull(roleRepository);
    }


    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void inValidateCategoryRequest() throws Exception {
        server.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidAddCategoryRequest_1()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void validateCategoryRequest() throws Exception {
        assertThat(categoryRepository.findAll().size(), is(0));
        server.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddCategoryRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk());
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(categoryRepository.findAll().get(0).getNameOfCategory(), is("Horror"));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void duplicateCategoryTest() throws Exception {
        assertThat(categoryRepository.findAll().size(), is(0));
        server.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddCategoryRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk());
        server.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddCategoryRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Category with name has already exist"))
                .andExpect(status().isConflict());
        assertThat(categoryRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteCategory() throws Exception {
        long categoryId = getCategoryFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        server.perform(delete("/category/" + categoryId))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(categoryRepository.findAll().size(), is(0));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteCategoryWithBook() throws Exception {
        long categoryId = getCategoryWithBookFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        server.perform(delete("/category/" + categoryId))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(categoryRepository.findAll().size(), is(0));
        assertThat(booksRepository.findAll().size(), is(2));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"ADMIN"})
    public void deleteNotExistCategory() throws Exception {
        getCategoryFromDb();
        assertThat(categoryRepository.findAll().size(), is(1));
        server.perform(delete("/category/" + 1000))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertThat(categoryRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findByIdNotExistCategory() throws Exception {
        getCategoryFromDb();
        assertThat(categoryRepository.findAll().size(), is(1));
        server.perform(get("/category/" + 5555))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findById() throws Exception {
        long categoryId = getCategoryFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        server.perform(get("/category/" + categoryId))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.nameOfCategory").value("Drama"))
                .andExpect(status().isOk());
        assertThat(categoryRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAll() throws Exception {
        long authorId = getCategoryFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        server.perform(get("/category"))
                .andDo(print())
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].nameOfCategory").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(authorId))
                .andExpect(jsonPath("$[0].nameOfCategory").value("Drama"))
                .andExpect(status().isOk());
        assertThat(categoryRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllCategoryBooksAndCategoryNotExist() throws Exception {
        getCategoryFromDb();
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(get("/category/" + 1212121 + "/books"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllCategoryEmptyList() throws Exception {
        long categoryId = getCategoryFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(0));
        server.perform(get("/category/" + categoryId + "/books")) //
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Test12345@user.com")
    public void findAllCategoryBook() throws Exception {
        long categoryId = getCategoryWithBookFromDb().getId();
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(booksRepository.findAll().size(), is(2));
        server.perform(get("/category/" + categoryId + "/books"))
                .andDo(print())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(jsonPath("$[*].name").isNotEmpty())
                .andExpect(jsonPath("$[*].description").isNotEmpty())
                .andExpect(status().isOk());
    }

    private Category getCategoryFromDb() {
        Category category = new Category();
        category.setNameOfCategory("Drama");
        return categoryRepository.save(category);
    }

    private Category getCategoryWithBookFromDb() {
        Category gothicCategory = new Category();
        ArrayList<Book> books = new ArrayList<>();
        gothicCategory.setBook(books);
        gothicCategory.setNameOfCategory("Gothic fiction");

        Author author = new Author("H.P.Lovecraft");
        author = authorRepository.save(author);

        Book bookTheCthulhu = new Book();
        bookTheCthulhu.setName("The Call of Cthulhu");
        bookTheCthulhu.setDescription("A monster of vaguely anthropoid outline, but with an octopus-like head's");
        bookTheCthulhu.setAuthor(author);
        bookTheCthulhu = booksRepository.save(bookTheCthulhu);

        gothicCategory.getBook().add(bookTheCthulhu);

        Author author1 = new Author("M.G Lewis");
        author1 = authorRepository.save(author1);

        Book bookTheMonk = new Book();
        bookTheMonk.setName("The Monk");
        bookTheMonk.setDescription("The Monk: A Romance is a Gothic novel by Matthew Gregory Lewis, published in 1796.");
        bookTheMonk.setAuthor(author1);
        bookTheMonk = booksRepository.save(bookTheMonk);

        gothicCategory.getBook().add(bookTheMonk);

        return categoryRepository.save(gothicCategory);
    }

    private String validAddCategoryRequest() {
        return "{" +
                " \"nameOfCategory\" : \"Horror\"" +
                "}";
    }

    private String inValidAddCategoryRequest_1() {
        return "{" +
                " \"nameOfCategory\" : \"\"" +
                "}";
    }

}
