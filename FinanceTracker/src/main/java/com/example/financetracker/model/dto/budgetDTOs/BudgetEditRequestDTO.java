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
public class BudgetEditRequestDTO {

    @Min(value = 1, message = "Invalid budget id.")
    private int budgetId;

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid limit.")
    @Min(value = 0, message = "Invalid limit.")
    @Digits(integer = 13, fraction = 2, message = "Invalid limit.")
    private BigDecimal maxLimit;

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    @NotNull(message = "Invalid note.")
    private String note;

    @NotEmpty
    private Set<Integer> categoryIds;

    @FutureOrPresent(message = "Invalid end date.")
    private LocalDate endDate;
}
