package com.yaskovich.battleship.exceptions;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class ExceptionInformation {
    HttpStatus status;
    Integer code;
    String message;
}
