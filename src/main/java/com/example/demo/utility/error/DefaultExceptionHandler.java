package com.example.demo.utility.error;

import com.example.demo.utility.exception.ConflictException;
import com.example.demo.utility.exception.NoContentException;
import com.example.demo.utility.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    @ResponseBody
    public GeneralError conflictExceptionHandler(ConflictException ex) {
        return new GeneralError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public GeneralError notFoundExceptionHandler(NotFoundException ex) {
        return new GeneralError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(NoContentException.class)
    @ResponseBody
    public GeneralError noContentExceptionHandler(NoContentException ex) {
        return new GeneralError(ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();

        Errors errors = new Errors();

        for (FieldError fieldError : bindingResult.getFieldErrors())
            errors.addError(deCamelCase(fieldError.getField()), fieldError.getCode(), fieldError.getDefaultMessage());

        return ResponseEntity.unprocessableEntity().body(errors);
    }

    private String deCamelCase(String s) {

        StringBuilder builder = new StringBuilder();

        char[] array = s.toCharArray();

        for (char c : array) {
            if (Character.isUpperCase(c))
                builder.append("_").append(Character.toLowerCase(c));
            else
                builder.append(c);
        }
        return builder.toString();
    }
}