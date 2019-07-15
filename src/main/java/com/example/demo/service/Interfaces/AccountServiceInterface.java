package com.example.demo.service.Interfaces;

import com.example.demo.model.DTO.Account.AccountDto;
import com.example.demo.model.DTO.Account.AccountDtoWithEmailAndPassword;
import com.example.demo.model.DTO.Account.AccountDtoWithNameAndEmail;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.TokenDto;

import java.util.List;

public interface AccountServiceInterface {

    IDDto createNewAccount(AccountDto accountDto);

    TokenDto getBasicAuthorizationToken(AccountDtoWithEmailAndPassword accountDtoWithEmailAndPassword);

    AccountDtoWithNameAndEmail getInfo();

    List<AccountDtoWithNameAndEmail> getAllAccounts();

    List<BookDto> getAllBook();

    List<BookDto> addReadBook(IDDto idDto);

    List<BookDto> removeReadBook(IDDto idDto);

    AccountDtoWithNameAndEmail changeRole(Long accountId, Long roleId);

}
