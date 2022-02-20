package com.example.financetracker.model.dto.userDTOs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserEditProfileRequestDTO {

    @NotNull(message = "Invalid first name.")
    private String firstName;

    @NotNull(message = "Invalid last name.")
    private String lastName;

    @NotNull(message = "Invalid gender.")
    @Pattern(regexp = "(?i)(male|female|other)", message = "Invalid gender.")
    private String gender;

    @PastOrPresent
    private LocalDate dateOfBirth;

}