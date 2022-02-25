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

    //@ValidPassword
    @NotBlank(message = "Invalid password.")
    private String newPassword;

//    @ValidPassword
    @NotBlank(message = "Invalid password.")
    private String confirmNewPassword;

}
