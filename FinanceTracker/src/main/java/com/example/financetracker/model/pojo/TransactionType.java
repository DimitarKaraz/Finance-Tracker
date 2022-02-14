package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "transaction_types")
@Getter
@Setter
@NoArgsConstructor
public class TransactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Invalid transaction type id.")
    private int transactionTypeId;

    @NotBlank(message = "Invalid transaction type name.")
    private String name;

}
