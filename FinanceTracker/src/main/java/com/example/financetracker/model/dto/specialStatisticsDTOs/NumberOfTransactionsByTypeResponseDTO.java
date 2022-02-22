package com.example.financetracker.model.dto.specialStatisticsDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
public class NumberOfTransactionsByTypeResponseDTO {

    private Map<String, Integer> transactionsByType;

}
