package com.example.financetracker.model.dto.budgetDTOs;

import com.example.financetracker.model.pojo.Category;
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

    @NotBlank(message = "Invalid name.")
    private String name;

    @NotNull(message = "Invalid limit.")
    @Min(value = 0, message = "Invalid limit.")
    @Digits(integer = 13, fraction = 2, message = "Invalid limit.")
    private BigDecimal limit;

    @Min(value = 1, message = "Invalid interval id.")
    private int intervalId;

    @NotNull(message = "Invalid start date.")
    @FutureOrPresent
    private LocalDate startDate;

    @Min(value = 1, message = "Invalid account id.")
    private int accountId;

    private String note;

    @NotEmpty
    private Set<Category> categories;

}
