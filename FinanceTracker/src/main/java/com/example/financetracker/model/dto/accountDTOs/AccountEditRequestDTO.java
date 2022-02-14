package com.example.financetracker.model.dto.accountDTOs;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AccountEditRequestDTO {

    @Min(1)
    private int userId;

    @Min(1)
    private int accountId;

    @NotBlank
    private String name;

    @NotNull
    private Currency currency;

    @NotNull
    private AccountType accountType;

    @NotNull
    @Min(0)
    @Digits(integer = 13, fraction = 2)
    private BigDecimal balance;

}
