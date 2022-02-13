package com.example.financetracker.model.dto.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequestDTO {

    private String email;
    private String password;
    private String confirmPassword;

}
