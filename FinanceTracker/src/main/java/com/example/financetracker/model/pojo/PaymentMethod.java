package com.example.financetracker.model.pojo;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentMethodId;

    @Column(name = "name")
    private String name;


}
