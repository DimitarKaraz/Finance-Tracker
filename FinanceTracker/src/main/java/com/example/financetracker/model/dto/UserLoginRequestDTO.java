package com.example.financetracker.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestDTO {

    private String email;
    private String password;
}
