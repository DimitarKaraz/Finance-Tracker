package com.example.financetracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<DTO> { //TODO: add interface

    private String message;
    private DTO dto;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
