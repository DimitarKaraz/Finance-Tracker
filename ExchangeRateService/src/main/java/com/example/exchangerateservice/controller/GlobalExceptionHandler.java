package com.example.exchangerateservice.controller;

import com.example.exchangerateservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleBadRequestException(NotFoundException e){
        return ResponseEntity.badRequest().body("Status: " + HttpStatus.NOT_FOUND + "" +
                                                "\nMessage: " + e.getMessage() + "" +
                                                "\nTimestamp: " + LocalDateTime.now());
    }

}
