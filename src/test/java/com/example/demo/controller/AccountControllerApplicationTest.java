package com.example.demo.controller;


import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.*;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.TokenDto;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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
public class AccountControllerApplicationTest {

    @Autowired
    private MockMvc server;

    @Autowired
    private ObjectMapper mapper;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Category category;


    @Before
    public void setUp() {
        accountRepository.deleteAll();
        booksRepository.deleteAll();
        authorRepository.deleteAll();
        categoriesRepository.deleteAll();
        accountRepository.deleteAll();
        category = null;
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
    @Transactional
    @WithMockUser(username = "Test12345@user.com",
            roles = {"USER", "ADMIN"})
    public void info() throws Exception {
        getAccountFromDb();
        server.perform(get("/account"))
                .andDo(print())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(accountRepository.findAll().size(), is(1));
        accountRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser(username = "Test12345@user.com")
    public void findAllWithoutRole() throws Exception {
        getAccountFromDb();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(get("/account/all"))
                .andDo(print())
                .andExpect(status().isForbidden());
        assertThat(accountRepository.findAll().size(), is(1));
    }

    @Test
    @Transactional
    @WithMockUser(username = "Test12345@user.com",
            roles = {"USER", "ADMIN"})
    public void findAll() throws Exception {
        getAccountFromDb();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(get("/account/all"))
                .andDo(print())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].email").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(accountRepository.findAll().size(), is(1));
    }

    @Test
    @WithMockUser(username = "Test12345@user.com",
            roles = {"USER", "ADMIN"})
    public void inValidateAccountRequest() throws Exception {
        server.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inValidCreateAccountRequest()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "Test123456@user.com")
    public void validateAccountRequest() throws Exception {
        assertThat(accountRepository.findAll().size(), is(0));
        server.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validCreateAccountRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk());
        assertThat(accountRepository.findAll().size(), is(1));
        assertThat(accountRepository.findAll().get(0).getName(), is("user"));

        server.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validCreateAccountRequestWithUpperCaseEmail()))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Account with this email already exist in system"))
                .andExpect(status().isConflict());
        assertThat(accountRepository.findAll().size(), is(1));
        assertThat(accountRepository.findAll().get(0).getName(), is("user"));
    }

    @Test
    @Transactional
    public void getBasicToken() throws Exception {
        getAccountFromDb();
        server.perform(post("/account/token")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(getBasicTokenRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.token").hasJsonPath())
                .andExpect(status().isOk());
        assertThat(accountRepository.findAll().size(), is(1));
    }

    @Test
    @Transactional
    @WithMockUser(username = "Test12345@user.com",
            roles = {"USER", "ADMIN"})
    public void addBook() throws Exception {
        getAccountFromDb();
        long bookId = getBookFromDb().getId();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(post("/account/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddBookRequest(bookId)))
                .andDo(print())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "Test12345@user.com", roles = "ADMIN")
    public void changeRole() throws Exception {
        long id = getAccountFromDb().getId();
        long roleId = roleRepository.findByRole("ADMIN").orElseThrow(() -> new NotFoundException("Role has not found")).getRoleId();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(put("/account/" + id + "/changeRole")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validChangeRoleRequest(roleId)))
                .andDo(print())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "Test12345@user.com",
            roles = {"USER", "ADMIN"})
    public void removeBook() throws Exception {
        String name = getAccountFromDb().getName();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(delete("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validRemoveBookRequest(
                        accountRepository.findByName(name)
                                .orElseThrow(() -> new NotFoundException("User has not found")).
                                getReadBooks().stream().findFirst()
                                .orElseThrow(() -> new NotFoundException("Book has not found")).getId())))
                .andDo(print())
                .andExpect(jsonPath("$[*].name").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void fullTest() throws Exception {
        //-------------------- CREATE USER(OK)--------------------//
        assertThat(accountRepository.findAll().size(), is(0));
        String accountIdAsString = server.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validCreateAccountRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(accountRepository.findAll().size(), is(1));
        assertThat(accountRepository.findAll().get(0).getName(), is("user"));

        //--------------------USER GET ID OF ACCOUNT--------------------//
        IDDto idDto = mapper.readValue(accountIdAsString, IDDto.class);

        //--------------------USER GET TOKEN(OK)--------------------//
        String contentAsString = server.perform(post("/account/token")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(getBasicTokenRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.token").hasJsonPath())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //--------------------USER GET TOKEN FROM CONTEXT--------------------//
        TokenDto tokenDto = mapper.readValue(contentAsString, TokenDto.class);
        String auth = "Authorization";
        String tokenValue = tokenDto.getToken();

        //--------------------USER GET INFO ABOUT USER(OK)--------------------//
        server.perform(get("/account")
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(status().isOk());

        //--------------------USER GET INFO ABOUT ALL USER(FORBIDDEN)--------------------//
        server.perform(get("/account/all")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(status().isForbidden());

        //--------------------USER GET ALL BOOK(OK)--------------------//
        server.perform(get("/account/allBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(idDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));

        //--------------------USER ADD BOOK(OK)--------------------//
        long bookId = getBookFromDb().getId();
        assertThat(accountRepository.findById(idDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));
        server.perform(post("/account/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddBookRequest(bookId))
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(idDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(1));

        //--------------------USER REMOVE BOOK(OK)--------------------//
        assertThat(accountRepository.findById(idDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(1));
        server.perform(delete("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validRemoveBookRequest(bookId))
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(idDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));

        //--------------------USER CHANGE ROLE(FORBIDDEN)--------------------//
        long roleId = roleRepository.findByRole("ADMIN").orElseThrow(() -> new NotFoundException("Role has not found")).getRoleId();
        assertThat(accountRepository.findAll().size(), is(1));
        server.perform(put("/account/" + idDto.getId() + "/changeRole")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validChangeRoleRequest(roleId))
                .header(auth, tokenValue))
                .andDo(print())
                .andExpect(status().isForbidden());

        //-------------------- CREATE ADMIN(OK)--------------------//
        assertThat(accountRepository.findAll().size(), is(1));
        String adminAccountIdAsString = server.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validCreateAdminAccountRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(accountRepository.findAll().size(), is(2));
        assertThat(accountRepository.findAll().get(1).getName(), is("admin"));

        //--------------------GET ADMIN ID OF ACCOUNT--------------------//
        IDDto adminIdDto = mapper.readValue(adminAccountIdAsString, IDDto.class);
        Account account = accountRepository.findById(adminIdDto.getId()).orElseThrow(() -> new NotFoundException("Admin has not found"));
        Role role = roleRepository.findByRole("ADMIN").orElseThrow(() -> new NotFoundException("Role has not found"));
        account.setRole(role);
        accountRepository.save(account);

        //--------------------GET ADMIN TOKEN(OK)--------------------//
        String adminContentAsString = server.perform(post("/account/token")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(getBasicAdminTokenRequest()))
                .andDo(print())
                .andExpect(jsonPath("$.token").hasJsonPath())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //--------------------GET ADMIN TOKEN FROM CONTEXT--------------------//
        TokenDto adminTokenDto = mapper.readValue(adminContentAsString, TokenDto.class);
        String adminTokenValue = adminTokenDto.getToken();

        //--------------------ADMIN GET INFO (OK)--------------------//
        server.perform(get("/account")
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(status().isOk());

        //--------------------ADMIN GET INFO ABOUT ALL USER (OK)--------------------//
        server.perform(get("/account/all")
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(status().isOk());

        //--------------------ADMIN GET ALL BOOK(OK)--------------------//
        server.perform(get("/account/allBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(adminIdDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));

        //--------------------ADMIN ADD BOOK(OK)--------------------//
        assertThat(accountRepository.findById(adminIdDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));
        server.perform(post("/account/addBook")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validAddBookRequest(bookId))
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(adminIdDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(1));

        //--------------------ADMIN REMOVE BOOK(OK)--------------------//
        server.perform(delete("/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validRemoveBookRequest(bookId))
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(accountRepository.findById(adminIdDto.getId())
                .orElseThrow(() -> new NotFoundException("User has not found")).getReadBooks().size(), is(0));

        //--------------------ADMIN CHANGE ROLE(OK)--------------------//
        assertThat(accountRepository.findAll().size(), is(2));
        server.perform(put("/account/" + idDto.getId() + "/changeRole")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validChangeRoleRequest(roleId))
                .header(auth, adminTokenValue))
                .andDo(print())
                .andExpect(status().isOk());

    }

    private String validRemoveBookRequest(Long bookId) {
        return String.format(
                "{" +
                        "\"id\" : \"%s\""
                        + "}", bookId);
    }

    private String validAddBookRequest(Long id_1) {
        return String.format(
                "{" +
                        "\"id\" : \"%s\""
                        + "}", id_1);
    }

    private String inValidCreateAccountRequest() {
        return "{" +
                " \"name\" : \"\"," +
                " \"email\" : \"User1@gmail.com\"," +
                " \"password\" : \"12345\"" +
                "}";
    }

    private String validCreateAccountRequest() {
        return "{" +
                " \"name\" : \"user\"," +
                " \"email\" : \"Test12345@user.com\"," +
                " \"password\" : \"Ra.123456789\"" +
                "}";
    }

    private String validCreateAdminAccountRequest() {
        return "{" +
                " \"name\" : \"admin\"," +
                " \"email\" : \"Admin123@gmail.com\"," +
                " \"password\" : \"Admin.123\"," +
                "\"role\":{\"role\":\"ADMIN\"}" +
                "}";
    }

    private String validCreateAccountRequestWithUpperCaseEmail() {
        return "{" +
                " \"name\" : \"user\"," +
                " \"email\" : \"TEST12345@USER.COM\"," +
                " \"password\" : \"Ra.123456789\"" +
                "}";
    }

    private String getBasicTokenRequest() {
        return "{" +
                " \"email\" : \"Test12345@user.com\"," +
                " \"password\" : \"Ra.123456789\"" +
                "}";
    }

    private String getBasicAdminTokenRequest() {
        return "{" +
                " \"email\" : \"Admin123@gmail.com\"," +
                " \"password\" : \"Admin.123\"" +
                "}";
    }

    private String validChangeRoleRequest(Long roleId) {
        return String.format("{" +
                " \"roleId\" : \"%s\""
                + "}", roleId);


    }

    private Account getAccountFromDb() {
        Account account = new Account();
        account.setName("test");
        account.setEmail("Test12345@user.com");
        account.setPassword(passwordEncoder.encode("Ra.123456789"));
        if (roleRepository.findByRole("USER").isPresent()) {
            Role role = roleRepository.findByRole("USER").get();
            account.setRole(role);
            roleRepository.save(role);
        }


        Author author = new Author("Church");
        authorRepository.save(author);

        Book book = new Book("Witch Hammer", "How to find and identify evil");
        book.setAuthor(author);

        account.addBook(book);
        booksRepository.save(book);

        Book book1 = new Book("Bible", "Holy Word");
        book1.setAuthor(author);

        account.addBook(book1);
        booksRepository.save(book1);

        return accountRepository.save(account);
    }


    private Book getBookFromDb() {
        Author author = new Author();
        author.setFullName("Антон Гепало");
        author = authorRepository.save(author);

        Book book = new Book();
        book.setName("Як вийти");
        book.setDescription("Ніяк");
        book.setAuthor(author);

        this.category = new Category();
        category.setNameOfCategory("Комедия");
        category = categoriesRepository.save(category);
        book.addCategory(category);

        return booksRepository.save(book);
    }

}
