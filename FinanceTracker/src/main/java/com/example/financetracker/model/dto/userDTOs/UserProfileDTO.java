package com.example.financetracker.model.dto.userDTOs;

import com.example.financetracker.model.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {

    @Min(value = 1, message = "Invalid user id.")
    private int userId;

    @NotBlank(message = "Invalid first name.")
    private String firstName;

    @NotBlank(message = "Invalid last name.")
    private String lastName;

    //Validated by @JsonProperty
    private User.Gender gender;

    //todo somehow validate date
    private LocalDate dateOfBirth;

    @NotBlank(message = "Invalid email.")
    private String email;

    //no validation needed?
    private String profileImageUrl;


}
