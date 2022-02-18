package com.example.financetracker.model.pojo;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;

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

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    public Transaction(Transaction transaction) {
        this.transactionType = transaction.getTransactionType();
        this.amount = transaction.getAmount();
        this.category = transaction.getCategory();
        this.account = transaction.getAccount();
        this.dateTime = transaction.getDateTime();
    }

    public Transaction(RecurrentTransaction recurrentTransaction){
        this.account = recurrentTransaction.getAccount();
        this.amount = recurrentTransaction.getAmount();
        this.category = recurrentTransaction.getCategory();
        this.paymentMethod = recurrentTransaction.getPaymentMethod();
        this.transactionType = recurrentTransaction.getTransactionType();
        this.dateTime = LocalDateTime.now();
    }

}
