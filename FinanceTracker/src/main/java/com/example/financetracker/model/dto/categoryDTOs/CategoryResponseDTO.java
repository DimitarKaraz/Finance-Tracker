package com.example.financetracker.model.dto.categoryDTOs;

import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.TransactionType;
import com.example.financetracker.model.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
@Component
@Getter
@Setter
@NoArgsConstructor
public class CategoryResponseDTO {

    private int categoryId;
    private String name;
    private CategoryIcon categoryIcon;
    private TransactionType transactionType;
    private int userId;
}
