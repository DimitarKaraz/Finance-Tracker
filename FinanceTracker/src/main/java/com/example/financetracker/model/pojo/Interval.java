package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "intervals")
@Getter
@Setter
@NoArgsConstructor
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
