package com.example.financetracker.model.dto.budgetDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@Getter
@Setter
@NoArgsConstructor
public class BudgetResponseDTO {

    private int budgetId;

    private String name;

    private BigDecimal amountSpent;

    private BigDecimal maxLimit;

    private int intervalId;

    private LocalDate startDate;

    private int accountId;

    private String note;

    private Set<Integer> categoryIds;
}
