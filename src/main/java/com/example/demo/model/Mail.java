package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Mail {

    private String from;
    private String to;
    private String subject;
    private String content;

    private Map<String, Object> model;

}
