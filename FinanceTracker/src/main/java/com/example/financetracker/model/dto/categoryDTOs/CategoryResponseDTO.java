package com.example.financetracker.model.dto.categoryDTOs;

import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
@Component
@Getter
@Setter
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {

    private int categoryId;

    private Integer userId;

    private String name;

    private TransactionType transactionType;

    private CategoryIcon categoryIcon;

}
