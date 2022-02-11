package com.example.financetracker.controller;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AbstractController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequestException(BadRequestException e){

        return new ErrorDTO(e.getMessage());

    }

}
