package com.example.financetracker.exceptions;

public class FileTransferException extends RuntimeException{

    public FileTransferException(String exceptionMessage){
        super(exceptionMessage);
    }
}
