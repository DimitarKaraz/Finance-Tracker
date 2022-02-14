package com.example.financetracker.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Invalid currency id.")
    private int currencyId;

    @Column(name = "name")
    @NotBlank(message = "Invalid name.")
    private String name;

    @Column(name = "abbreviation")
    @NotBlank(message = "Invalid abbreviation.")
    private String abbreviation;


}
