package com.example.financetracker.model.dto.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserProfileDTO {

    @Min(value = 1, message = "Invalid user id.")
    private int userId;

    @NotBlank(message = "Invalid email.")
    private String email;

    @NotNull(message = "Invalid first name.")
    private String firstName;

    @NotNull(message = "Invalid last name.")
    private String lastName;

    @NotNull(message = "Invalid gender.")
    @Pattern(regexp = "(?i)(male|female|other)", message = "Invalid gender.")
    private String gender;

    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotNull(message = "Invalid url.")
    private String profileImageUrl;


}
