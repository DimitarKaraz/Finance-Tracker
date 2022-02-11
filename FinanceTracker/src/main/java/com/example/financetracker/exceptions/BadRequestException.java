package com.example.financetracker.exceptions;


public class BadRequestException extends Exception {

    public BadRequestException(String exceptionMessage){
        super(exceptionMessage);
    }

}

