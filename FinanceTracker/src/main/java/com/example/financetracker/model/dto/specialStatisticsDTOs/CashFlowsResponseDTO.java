package com.example.financetracker.model.dto.specialStatisticsDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
public class CashFlowsResponseDTO {
    //     accountName  totalIncome, totalExpense
    private Map<String, Map<String, BigDecimal>> cashFlowsForAccounts;

}
