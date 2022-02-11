package com.example.financetracker.exceptions;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String exceptionMessage){
        super(exceptionMessage);
    }

}

