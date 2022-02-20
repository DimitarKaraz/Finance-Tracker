package com.example.financetracker.model.dto.budgetDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@Getter
@Setter
@NoArgsConstructor
public class BudgetCreateRequestDTO {

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid limit.")
    @Min(value = 1, message = "Invalid limit.")
    @Digits(integer = 13, fraction = 2, message = "Invalid limit.")
    private BigDecimal maxLimit;

    @NotEmpty(message = "Invalid category ids.")
    private Set<Integer> categoryIds;

    @NotNull(message = "Invalid note.")
    private String note;

    @NotNull(message = "Invalid start date.")
    @FutureOrPresent(message = "Invalid start date.")
    private LocalDate startDate;

    @FutureOrPresent(message = "Invalid end date.")
    private LocalDate endDate;

    @Min(value = 1, message = "Invalid interval id.")
    private Integer intervalId;

}
