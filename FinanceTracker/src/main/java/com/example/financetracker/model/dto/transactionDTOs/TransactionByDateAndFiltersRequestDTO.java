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
public class TransactionByDateAndFiltersRequestDTO {

    @NotNull(message = "Start date cannot be null.")
    private LocalDate start_date;

    @NotNull(message = "End date cannot be null.")
    private LocalDate end_date;

    @Min(value = 1, message = "Invalid amount.")
    private Integer accountId;

    @Min(value = 1, message = "Invalid amount.")
    private Integer paymentMethodId;

    private Set<Integer> categoryIds;

    @Min(value = 1, message = "Invalid amount.")
    private Integer transactionTypeId;

    @Min(value = 1, message = "Invalid amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amountMin;

    @Min(value = 1, message = "Invalid amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount.")
    private BigDecimal amountMax;

}
