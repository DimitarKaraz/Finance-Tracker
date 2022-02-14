package com.example.financetracker.exceptions;

public class MethodArgumentNotValidException extends BadRequestException{
    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}
