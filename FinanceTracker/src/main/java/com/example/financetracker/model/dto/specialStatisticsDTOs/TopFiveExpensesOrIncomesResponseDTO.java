package com.example.financetracker.model.dto.specialStatisticsDTOs;

import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TopFiveExpensesOrIncomesResponseDTO {

    private Map<String, BigDecimal> topFiveExpensesOrIncomes;

}
