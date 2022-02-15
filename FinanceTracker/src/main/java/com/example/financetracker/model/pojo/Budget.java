package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int budgetId;

    private String name;

    private BigDecimal amountSpent;

    private BigDecimal limit;

    @ManyToOne
    @JoinColumn(name = "interval_id")
    private Interval interval;

    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String note;

}
