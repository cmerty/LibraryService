package com.example.demo.utility.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Errors extends HashMap<String, List<Errors.Error>> {

    void addError(String field, String code, String msg) {

        if (!containsKey(field))
            put(field, new ArrayList<>());

        get(field).add(Error.with(code, msg));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Error {

        private String code;

        private String message;

        static Error with(String code, String message) {
            return new Error(code, message);
        }
    }
}