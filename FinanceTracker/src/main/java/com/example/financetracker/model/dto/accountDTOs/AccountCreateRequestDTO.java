package com.example.financetracker.model.dto.accountDTOs;


import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AccountCreateRequestDTO {

    private String name;
    private Currency currency;
    private AccountType accountType;
    private BigDecimal balance;
    //todo maybe add userId
}
