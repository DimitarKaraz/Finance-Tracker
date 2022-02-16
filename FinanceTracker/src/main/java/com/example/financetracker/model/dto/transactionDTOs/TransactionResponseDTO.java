package com.example.financetracker.model.dto.transactionDTOs;

import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.PaymentMethod;
import com.example.financetracker.model.pojo.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TransactionResponseDTO {

    private int transactionId;

    private TransactionType transactionType;

    private BigDecimal amount;

    private String accountName;

    private Currency currency;

    private Category category;

    private PaymentMethod paymentMethod;

    private LocalDateTime dateTime;

}
