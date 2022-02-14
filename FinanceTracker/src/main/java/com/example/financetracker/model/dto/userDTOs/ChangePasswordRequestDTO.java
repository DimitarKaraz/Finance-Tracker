package com.example.financetracker.model.dto.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.jms.JMSPasswordCredential;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDTO {

    @Min(value = 1, message = "Invalid user id.")
    private int userId;

    @NotBlank(message = "Invalid password.")
    private String oldPassword;

    @NotBlank(message = "Invalid password.")
    private String newPassword;

    @NotBlank(message = "Invalid password.")
    private String confirmNewPassword;

}
