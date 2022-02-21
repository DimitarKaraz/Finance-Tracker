package com.example.financetracker.model.pojo;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "intervals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Invalid interval id.")
    private int intervalId;

    @Column
    private int days;

    @Column
    private String name;

}
