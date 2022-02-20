package com.example.financetracker.model.dto.accountDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AccountEditRequestDTO {

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid balance.")
    @Min(value = 0, message = "Invalid balance.")
    @Digits(integer = 13, fraction = 2, message = "Invalid balance.")
    private BigDecimal balance;

    @Min(value = 1, message = "Invalid currency id.")
    private int currencyId;

    @Min(value = 1, message = "Invalid account type id.")
    private int accountTypeId;


}
