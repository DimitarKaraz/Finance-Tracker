package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "recurrent_transactions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RecurrentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recurrentTransactionId;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "interval_id")
    private Interval interval;

    @Column(name = "interval_count")
    private int intervalCount;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "remaining_payments")
    private Integer remainingPayments;

}
