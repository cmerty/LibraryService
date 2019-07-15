package com.example.demo.utility.exception;

import lombok.Getter;
import lombok.Setter;

public class NotFoundException extends RuntimeException {

    @Getter
    @Setter
    private String message;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
