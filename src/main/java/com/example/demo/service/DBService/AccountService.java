package com.example.demo.service.DBService;

import com.example.demo.service.Interfaces.AccountServiceInterface;
import com.example.demo.utility.exception.ConflictException;
import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.Book;
import com.example.demo.model.DTO.Account.AccountDto;
import com.example.demo.model.DTO.Account.AccountDtoWithEmailAndPassword;
import com.example.demo.model.DTO.Account.AccountDtoWithNameAndEmail;
import com.example.demo.model.DTO.Book.BookDto;
import com.example.demo.model.DTO.ID.IDDto;
import com.example.demo.model.DTO.TokenDto;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BooksRepository;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Profile("Db")
@RequiredArgsConstructor
public class AccountService implements AccountServiceInterface {

    private final BooksRepository booksRepository;

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public IDDto createNewAccount(AccountDto accountDto) {

        if (accountRepository.findByEmailIgnoreCase(accountDto.getEmail()).isPresent())
            throw new ConflictException("Account with this email already exist in system");

        Account account = new Account();
        account.setName(accountDto.getName());
        account.setEmail(accountDto.getEmail());
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));

        if (roleRepository.findByRole("USER").isPresent())
            account.setRole(roleRepository.findByRole("USER").get());

        accountRepository.save(account);
        return new IDDto(account.getId());
    }

    @Override
    public TokenDto getBasicAuthorizationToken(AccountDtoWithEmailAndPassword accountDtoWithEmailAndPassword) {

        Account account = getAccountByEmail(accountDtoWithEmailAndPassword.getEmail());

        if (passwordEncoder.matches(accountDtoWithEmailAndPassword.getPassword(), account.getPassword())) {

            String s = account.getEmail() + ":" + accountDtoWithEmailAndPassword.getPassword();

            byte[] token = Base64.getEncoder().encode(s.getBytes());
            String tokenString = new String(token);

            return new TokenDto("Basic " + tokenString);
        } else
            throw new NotFoundException("User has not found");
    }

    @Override
    public AccountDtoWithNameAndEmail getInfo() {

        String email = getSecurityContextName();

        Account account = getAccountByEmail(email);

        return new AccountDtoWithNameAndEmail(account.getName(), account.getEmail());
    }

    @Override
    public List<AccountDtoWithNameAndEmail> getAllAccounts() {

        List<Account> accountDtoList = accountRepository.findAll();

        return accountDtoList.stream()
                .map(account -> new AccountDtoWithNameAndEmail(account.getName(), account.getEmail()))
                .collect(toList());
    }

    @Override
    public List<BookDto> getAllBook() {

        String email = getSecurityContextName();

        Account account = getAccountByEmail(email);

        return account.getReadBooks()
                .stream()
                .map(book -> new BookDto(book.getName(), book.getDescription())).collect(toList());
    }

    @Override
    public List<BookDto> addReadBook(IDDto idDto) {

        String email = getSecurityContextName();

        Account account = getAccountByEmail(email);

        Book book = getBookById(idDto);

        account.getReadBooks().add(book);

        accountRepository.save(account);

        return account.getReadBooks()
                .stream()
                .map(books -> new BookDto(books.getName(), books.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDto> removeReadBook(IDDto idDto) {

        String email = getSecurityContextName();

        Account account = getAccountByEmail(email);

        Book book = getBookById(idDto);

        account.removeBook(book);
        accountRepository.save(account);

        return account.getReadBooks()
                .stream()
                .map(books -> new BookDto(books.getName(), books.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public AccountDtoWithNameAndEmail changeRole(Long accountId, Long roleId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("User has not found"));

        account.setRole(
                roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException("Role has not found")));

        accountRepository.save(account);
        return new AccountDtoWithNameAndEmail(account.getName(), account.getEmail());
    }

    private String getSecurityContextName() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    private Account getAccountByEmail(String email) {
        return accountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Account has not found"));
    }

    private Book getBookById(IDDto idDto) {
        return booksRepository.
                findById(idDto.getId()).orElseThrow(() -> new NotFoundException("Book has not found"));
    }

}
