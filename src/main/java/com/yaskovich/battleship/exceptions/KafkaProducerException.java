package com.yaskovich.battleship.exceptions;

public class KafkaProducerException extends RuntimeException{
    public KafkaProducerException(String message) {
        super(message);
    }
}
