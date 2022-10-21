package com.yaskovich.battleship.exceptions.handler;

import com.yaskovich.battleship.exceptions.ExceptionInformation;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionInformation handlePatientResourceException(RuntimeException ex) {
        return new ExceptionInformation(BAD_REQUEST, BAD_REQUEST.value(), ex.getMessage());
    }
}
