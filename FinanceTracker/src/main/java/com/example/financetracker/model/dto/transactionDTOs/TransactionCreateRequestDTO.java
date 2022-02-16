package com.example.financetracker.model.dto.transactionDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TransactionCreateRequestDTO {

    @Min(value = 1, message = "Invalid transaction type id.")
    private int transactionTypeId;

    @NotNull(message = "Invalid amount.")
    @Min(value = 0, message = "Invalid amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amount;

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @Min(value = 1, message = "Invalid category id.")
    private int categoryId;

    @Min(value = 1, message = "Invalid payment method id.")
    private int paymentMethodId;

}
