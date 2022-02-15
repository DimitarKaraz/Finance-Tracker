package com.example.financetracker.model.dto.categoryDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Component
@Getter
@Setter
@NoArgsConstructor
public class CategoryCreateRequestDTO {

    @NotBlank(message = "Invalid category name.")
    private String name;

    @Min(value = 1, message = "Invalid category icon id.")
    private int categoryIconId;

    @Min(value = 1, message = "Invalid transaction type id.")
    private int transactionTypeId;

    @Min(value = 1, message = "Invalid user id.")
    private int userId;


}
