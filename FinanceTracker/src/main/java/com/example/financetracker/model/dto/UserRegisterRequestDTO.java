package com.example.financetracker.model.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequestDTO {

    @NotNull
    private String email;
    private String password;
    private String confirmPassword;

}
