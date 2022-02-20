package com.example.financetracker.model.dto.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Invalid password.")
    private String oldPassword;

    @NotBlank(message = "Invalid password.")
    private String newPassword;

    @NotBlank(message = "Invalid password.")
    private String confirmNewPassword;

}
