package com.example.demo.utility.exception;

import lombok.Getter;
import lombok.Setter;

public class ConflictException extends RuntimeException {

    @Getter
    @Setter
    private String message;

    public ConflictException(String message) {
        super(message);
        this.message = message;
    }
}
