package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int budgetId;

    @Column(name = "name")
    private String name;

    @Column(name = "amount_spent")
    private BigDecimal amountSpent;

    @Column(name = "max_limit")
    private BigDecimal maxLimit;

    @ManyToOne
    @JoinColumn(name = "interval_id")
    private Interval interval;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "note")
    private String note;

    @ManyToMany
    @JoinTable(
            name = "budgets_have_categories",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

}
