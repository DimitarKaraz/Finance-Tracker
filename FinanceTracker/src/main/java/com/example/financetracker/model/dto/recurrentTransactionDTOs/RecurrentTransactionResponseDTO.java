package com.example.financetracker.model.dto.recurrentTransactionDTOs;

import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.Interval;
import com.example.financetracker.model.pojo.PaymentMethod;
import com.example.financetracker.model.pojo.TransactionType;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurrentTransactionResponseDTO {

    private int recurrentTransactionId;

    private String name;

    private String accountName;

    private Currency currency;

    private BigDecimal amount;

    private TransactionType transactionType;

    private CategoryResponseDTO categoryResponseDTO;

    private PaymentMethod paymentMethod;

    private LocalDate startDate;

    private Interval interval;

    private int intervalCount;

    private LocalDate endDate;

    private Integer remainingPayments;

}
