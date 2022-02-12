package com.example.financetracker.model.dto.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDTO {

    private int userId;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

}
