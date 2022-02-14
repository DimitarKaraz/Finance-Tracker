package com.example.financetracker.controller;

import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //todo think of a better name for method
    //todo change return type to ResponseEntity
    //todo think of a better url
    @GetMapping("/{id}")
    public List<Category> getAllCategoriesByUserId(@PathVariable int id){
        return categoryService.getAllCategoriesByUserId(id);
    }

    @PostMapping("/create")
    public Category createCategory(@RequestBody CategoryCreateRequestDTO requestDTO){
        return categoryService.createCategory(requestDTO);
    }

}
