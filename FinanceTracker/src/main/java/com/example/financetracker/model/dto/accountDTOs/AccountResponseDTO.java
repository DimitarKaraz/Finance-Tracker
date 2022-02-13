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
public class AccountResponseDTO {

    private int accountId;
    private int userId;
    private AccountType accountType;
    private Currency currency;
    private BigDecimal balance;
    private String name;

}
