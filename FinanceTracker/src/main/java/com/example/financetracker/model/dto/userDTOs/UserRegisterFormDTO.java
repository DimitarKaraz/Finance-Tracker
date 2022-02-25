package com.example.financetracker.model.dto.userDTOs;

import com.example.financetracker.passwordValidators.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
public class UserRegisterFormDTO implements Serializable {

    @NotBlank(message = "Invalid email.")
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x" +
            "08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x" +
            "7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a" +
            "-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*" +
            "[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]" +
            "?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]" +
            "?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e" +
            "-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
            message = "Invalid email.")
    private String email;

    // For easier demonstration, the @ValidPassword is commented out.

    /*@ValidPassword(message = "Password must be between 8-50 symbols, and contain " +
            "one lower-case letter, one upper-case letter, one digit, one special symbol, no whitespaces, " +
            "and no 4-or-more ordered letters or digits.")*/
    @NotBlank(message = "Invalid password.")
    private String password;

    /*@ValidPassword(message = "Password must be between 8-50 symbols, and contain " +
            "one lower-case letter, one upper-case letter, one digit, one special symbol, no whitespaces, " +
            "and no 4-or-more ordered letters or digits.")*/
    @NotBlank(message = "Invalid password.")
    private String confirmPassword;

}
