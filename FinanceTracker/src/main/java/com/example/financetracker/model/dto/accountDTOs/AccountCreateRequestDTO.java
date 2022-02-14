package com.example.financetracker.model.dto.accountDTOs;


import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AccountCreateRequestDTO {

    private int userId;
    @NotNull
    @NotBlank
    private String name;
    private Currency currency;
    private AccountType accountType;
    private BigDecimal balance;

}
