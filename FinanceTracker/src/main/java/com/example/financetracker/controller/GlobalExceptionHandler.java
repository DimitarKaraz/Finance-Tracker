package com.example.financetracker.controller;

import com.example.financetracker.exceptions.*;
import com.example.financetracker.model.dto.ExceptionDTO;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleBadRequestException(BadRequestException e){
        return ResponseEntity.badRequest().body(new ExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleInvalidFormatException(InvalidFormatException e){
        return ResponseEntity.status(400).body(new ExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(UnauthorizedException e){
        return ResponseEntity.status(401).body(new ExceptionDTO(HttpStatus.UNAUTHORIZED, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(404).body(new ExceptionDTO(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ResponseEntity<ExceptionDTO> handleNotImplementedException(NotImplementedException e){
        return ResponseEntity.status(501).body(new ExceptionDTO(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), LocalDateTime.now()));
    }

}
