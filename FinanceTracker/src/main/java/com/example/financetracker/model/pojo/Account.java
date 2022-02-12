package com.example.financetracker.model.pojo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

//    @OneToOne()
//    @JoinColumn(name = "currency_id")
//    private String currency;


}
