package com.example.financetracker.model.dto.accountDTOs;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AccountEditRequestDTO {

    @Min(value = 1, message = "Invalid user id.")
    private int userId;

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid currency.")
    @Valid
    private Currency currency;

    @NotNull(message = "Invalid account type.")
    @Valid
    private AccountType accountType;

    @NotNull(message = "Invalid balance.")
    @Min(value = 0, message = "Invalid balance.")
    @Digits(integer = 13, fraction = 2, message = "Invalid balance.")
    private BigDecimal balance;

}
