package com.example.demo.utility.exception;

import lombok.Getter;
import lombok.Setter;

public class NoContentException extends RuntimeException {

    @Getter
    @Setter
    private String message;

    public NoContentException(String message) {
        super(message);
        this.message = message;
    }
}
