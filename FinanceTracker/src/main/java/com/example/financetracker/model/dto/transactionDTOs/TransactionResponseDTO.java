package com.example.financetracker.model.dto.transactionDTOs;

import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.PaymentMethod;
import com.example.financetracker.model.pojo.TransactionType;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TransactionResponseDTO {

    private int transactionId;

    private String accountName;

    private Currency currency;

    private BigDecimal amount;

    private TransactionType transactionType;

    private CategoryResponseDTO categoryResponseDTO;

    private PaymentMethod paymentMethod;

    private LocalDateTime dateTime;

}
