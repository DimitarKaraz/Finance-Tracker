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
public class CategoryEditRequestDTO {

    @Min(value = 1, message = "Invalid category id.")
    private int categoryId;

    @NotBlank(message = "Invalid category name.")
    private String name;

    @Min(value = 1, message = "Invalid category icon id.")
    private int categoryIconId;

}
