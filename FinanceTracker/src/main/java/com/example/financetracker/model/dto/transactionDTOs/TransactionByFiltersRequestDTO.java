package com.example.financetracker.model.dto.transactionDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TransactionByFiltersRequestDTO {

    @NotNull(message = "Start date cannot be null.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    private LocalDate endDate;

    @Min(value = 1, message = "Invalid account id.")
    private Integer accountId;

    @Min(value = 1, message = "Invalid transaction type id.")
    private Integer transactionTypeId;

    private Set<Integer> categoryIds;

    @Min(value = 1, message = "Invalid payment method id.")
    private Integer paymentMethodId;

    @Min(value = 1, message = "Invalid minimal amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amountMin;

    @Min(value = 1, message = "Invalid max amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amountMax;

}
