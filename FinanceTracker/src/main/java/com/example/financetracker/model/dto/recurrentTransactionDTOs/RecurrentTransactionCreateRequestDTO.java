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
public class RecurrentTransactionCreateRequestDTO {

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid amount.")
    @Min(value = 1, message = "Invalid amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amount;

    @Min(value = 1, message = "Invalid transaction type id.")
    private int transactionTypeId;

    @Min(value = 1, message = "Invalid category id.")
    private int categoryId;

    @Min(value = 1, message = "Invalid payment method id.")
    private int paymentMethodId;

    @FutureOrPresent(message = "Invalid start date.")
    @NotNull(message = "Invalid start date.")
    private LocalDate startDate;

    @Min(value = 1, message = "Invalid interval id.")
    private int intervalId;

    @Min(value = 1, message = "Invalid interval count id.")
    @Max(value = 99, message = "Invalid interval count id.")
    private int intervalCount;

    @FutureOrPresent(message = "Invalid end date.")
    private LocalDate endDate;

    @Min(value = 1, message = "Invalid remaining payments.")
    @Max(value = 999, message = "Invalid remaining payments.")
    private Integer remainingPayments;

}
