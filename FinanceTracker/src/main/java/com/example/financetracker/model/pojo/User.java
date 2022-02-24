package com.example.financetracker.model.pojo;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "last_login")
    private LocalDate lastLogin;

    @Column(name = "last_email_sent_on")
    private LocalDate lastEmailSentOn;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "is_enabled")
    private boolean isEnabled;

}
