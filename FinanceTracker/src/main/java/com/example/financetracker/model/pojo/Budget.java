package com.example.financetracker.model.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int budgetId;

    @Column(name = "name")
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "amount_spent")
    private BigDecimal amountSpent;

    @Column(name = "max_limit")
    private BigDecimal maxLimit;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @EqualsAndHashCode.Include
    private Account account;

    @Column(name = "note")
    private String note;

    @ManyToMany
    @JoinTable(
            name = "budgets_have_categories",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonManagedReference
    private Set<Category> categories;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "interval_id")
    private Interval interval;

}
