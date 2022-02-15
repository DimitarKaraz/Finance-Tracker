package com.example.financetracker.model.dto.userDTOs;

import com.example.financetracker.model.pojo.User;
import com.example.financetracker.utilities.javax_validation.EditUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {

    @Min(value = 1, message = "Invalid user id.", groups = EditUserRequest.class)
    private int userId;

    @NotBlank(message = "Invalid first name.")
    private String firstName;

    @NotBlank(message = "Invalid last name.")
    private String lastName;

    //Validated by @JsonProperty
    private User.Gender gender;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotBlank(message = "Invalid email.", groups = EditUserRequest.class)
    private String email;

    //no validation needed
    private String profileImageUrl;


}
