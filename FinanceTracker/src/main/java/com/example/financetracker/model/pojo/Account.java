package com.example.financetracker.model.pojo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String currency;

    private String type;

    //todo not sure if works?
    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "name")
    private String name;





}
