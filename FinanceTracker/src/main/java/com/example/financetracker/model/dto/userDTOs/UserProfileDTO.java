package com.example.financetracker.model.dto.userDTOs;

import com.example.financetracker.model.pojo.User;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private User.Gender gender;
    private LocalDate dateOfBirth;
    private String email;
    private String profileImageUrl;


}
