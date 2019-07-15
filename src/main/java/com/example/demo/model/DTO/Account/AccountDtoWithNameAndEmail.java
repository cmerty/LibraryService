package com.example.demo.model.DTO.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDtoWithNameAndEmail {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

}
