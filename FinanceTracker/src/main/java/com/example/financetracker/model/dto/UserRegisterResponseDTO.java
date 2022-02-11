package com.example.financetracker.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.OneToMany;


@Component
@Getter
@Setter
@NoArgsConstructor
public class UserRegisterResponseDTO {

    private int id;
    private String email;
    @OneToMany(mappedBy = "user_id")
    private List<Account> userAccounts;
    private String profileImageUrl;

}
