package com.example.financetracker.model.dto.recurrentTransactionDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class RecurrentTransactionEditRequestDTO {

    @Min(value = 1, message = "Invalid id.")
    private int recurrentTransactionTypeId;

    @Min(value = 1, message = "Invalid transaction type id.")
    private int transactionTypeId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid amount.")
    @Min(value = 1, message = "Invalid amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amount;

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @Min(value = 1, message = "Invalid category id.")
    private int categoryId;

    @Min(value = 1, message = "Invalid payment method id.")
    private int paymentMethodId;

    @FutureOrPresent(message = "Invalid end date.")
    private LocalDate endDate;

    //TODO: NULL or >= 1 .... TEST IT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Min(value = 1, message = "Invalid remaining payments.")
    private Integer remainingPayments;
}
