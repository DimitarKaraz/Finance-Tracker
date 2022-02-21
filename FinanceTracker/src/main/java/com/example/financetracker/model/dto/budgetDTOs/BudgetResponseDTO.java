package com.example.financetracker.model.dto.budgetDTOs;

import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.Interval;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDTO {

    private int budgetId;

    private String name;

    private BigDecimal amountSpent;

    private BigDecimal maxLimit;

    private Interval interval;

    private LocalDate startDate;

    private String accountName;

    private Currency currency;

    private String note;

    private Set<CategoryResponseDTO> categoryResponseDTOs;

    private LocalDate endDate;
}
