package com.example.financetracker.model.dto.categoryDTOs;

import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@Getter
@Setter
@NoArgsConstructor
public class CategoryCreateRequestDTO {

    @NotBlank(message = "Invalid category name.")
    private String name;

    @NotNull(message = "Invalid category icon.")
    @Valid
    private CategoryIcon categoryIcon;

    @NotNull(message = "Invalid transaction type.")
    @Valid
    private TransactionType transactionType;

    @Min(value = 1, message = "Invalid user id.")
    private int userId;


}
