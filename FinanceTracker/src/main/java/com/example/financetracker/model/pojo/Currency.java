package com.example.financetracker.model.pojo;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Invalid currency id.")
    private int currencyId;

    @Column(name = "name")
    @NotBlank(message = "Invalid currency name.")
    private String name;

    @Column(name = "abbreviation")
    @NotBlank(message = "Invalid currency abbreviation.")
    private String abbreviation;


}
