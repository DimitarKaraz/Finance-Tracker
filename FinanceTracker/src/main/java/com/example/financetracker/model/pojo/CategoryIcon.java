package com.example.financetracker.model.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "category_icons")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class CategoryIcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Invalid category icon id.")
    private int categoryIconId;

    @NotBlank(message = "Invalid category icon image url.")
    private String imageUrl;

}
