package com.example.demo.controller;

import com.example.demo.model.DTO.Account.AccountDto;
import com.example.demo.model.DTO.Account.AccountDtoWithEmailAndPassword;
import com.example.demo.model.DTO.Account.AccountDtoWithNameAndEmail;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.Role.RoleDto;
import com.example.demo.model.DTO.TokenDto;
import com.example.demo.service.Interfaces.AccountServiceInterface;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountServiceInterface accountServiceInterface;

    public AccountController(AccountServiceInterface accountServiceInterface) {
        this.accountServiceInterface = accountServiceInterface;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IDDto createNewAccount(@Valid @RequestBody AccountDto accountDto) {
        return accountServiceInterface.createNewAccount(accountDto);
    }

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TokenDto getBasicTokenAuthorization(@Valid @RequestBody AccountDtoWithEmailAndPassword accountDtoWithEmailAndPassword) {
        return accountServiceInterface.getBasicAuthorizationToken(accountDtoWithEmailAndPassword);
    }

    @GetMapping()
    public AccountDtoWithNameAndEmail info() {
        return accountServiceInterface.getInfo();
    }


    @GetMapping(value = "/all")
    public List<AccountDtoWithNameAndEmail> allAccounts() {
        return accountServiceInterface.getAllAccounts();
    }

    @GetMapping(value = "/allBook")
    public List<BookDto> getAllBooks() {
        return accountServiceInterface.getAllBook();
    }

    @PostMapping(value = "/addBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BookDto> addBook(@Valid @RequestBody IDDto idDto) {
        return accountServiceInterface.addReadBook(idDto);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BookDto> removeBook(@Valid @RequestBody IDDto idDto) {
        return accountServiceInterface.removeReadBook(idDto);
    }

    @PutMapping(value = "/{id}/changeRole", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public AccountDtoWithNameAndEmail changeRole(@PathVariable Long id, @Valid @RequestBody RoleDto role) {
        return accountServiceInterface.changeRole(id, role.getRoleId());
    }

}
