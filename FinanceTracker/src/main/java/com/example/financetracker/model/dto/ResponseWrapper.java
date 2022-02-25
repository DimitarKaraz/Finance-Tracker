package com.example.financetracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<DTOs> {    // Generic is for error-proofing

    private String message;
    private DTOs data;  //single DTO or Collection<DTO>
    private HttpStatus status;
    private LocalDateTime timestamp;

    public static <DTO> ResponseEntity<ResponseWrapper<DTO>> wrap(String message, DTO data, HttpStatus status) {
        return ResponseEntity.status(status.value()).body(new ResponseWrapper<>(message, data, status, LocalDateTime.now()));
    }

}
