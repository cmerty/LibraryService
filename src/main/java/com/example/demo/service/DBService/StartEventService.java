package com.example.demo.service.DBService;

import com.example.demo.utility.exception.NotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartEventService implements ApplicationListener<ApplicationReadyEvent> {

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public StartEventService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        seedData();
    }

    private void seedData() {
        if (roleRepository.findByRole("ADMIN").isEmpty()) {
            Role admin = new Role("ADMIN");
            roleRepository.save(admin);
        }
        if (roleRepository.findByRole("USER").isEmpty()) {
            Role user = new Role("USER");
            roleRepository.save(user);
        }

        if (accountRepository.findByName("admin").isEmpty()) {
            Account account = new Account();
            account.setName("admin");
            account.setEmail("Admin123@gmail.com");
            account.setPassword(passwordEncoder.encode("Admin.12345"));
            Role role = roleRepository.findByRole("ADMIN").orElseThrow(() -> new NotFoundException("Role has not found"));
            account.setRole(role);
            accountRepository.save(account);
        }
    }

}



