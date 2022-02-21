package com.example.financetracker.model.dto.budgetDTOs;

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
public class BudgetByFiltersDTO {

    @NotNull(message = "Start date cannot be null.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    private LocalDate endDate;

    @Min(value = 1, message = "Invalid account id.")
    private Integer accountId;

    @Min(value = 1, message = "Invalid interval id.")
    private Integer intervalId;

    private Set<Integer> categoryIds;

    @Min(value = 1, message = "Invalid min amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid min amount.")
    private BigDecimal amountMin;

    @Min(value = 1, message = "Invalid max amount.")
    @Digits(integer = 13, fraction = 2, message = "Invalid max amount.")
    private BigDecimal amountMax;

}