package com.example.financetracker.model.dto.userDTOs;

import com.example.financetracker.model.pojo.User;
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

    @Min(value = 1, message = "Invalid user id.")
    private int userId;

    @NotNull(message = "Invalid first name.")
    private String firstName;

    @NotNull(message = "Invalid last name.")
    private String lastName;

    //Validated by @JsonProperty
    private User.Gender gender;

    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotBlank(message = "Invalid email.")
    private String email;

    @NotNull(message = "Invalid url.")
    private String profileImageUrl;


}
