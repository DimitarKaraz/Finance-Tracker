package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryEditRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/categories/create")
    public ResponseEntity<ResponseWrapper<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Category created.",
                categoryService.createCategory(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseWrapper<LinkedHashMap<String, Object>>> getAllCategoriesOfCurrentUser
            (@RequestParam(name = "page", defaultValue = "0") int pageNumber){
        return ResponseWrapper.wrap("Categories for user retrieved.",
                categoryService.getAllCategoriesOfCurrentUser(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/categories/{category_id}")
    public ResponseEntity<ResponseWrapper<CategoryResponseDTO>> getCategoryById(@PathVariable("category_id") int id){
        return ResponseWrapper.wrap("Category " + id + " retrieved.",
                categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping("/categories/transaction_type/{transaction_type_id}")
    public ResponseEntity<ResponseWrapper<List<CategoryResponseDTO>>> getCategoriesOfCurrentUserByTransactionType(@PathVariable("transaction_type_id") int transactionTypeId){
        return ResponseWrapper.wrap("Categories for user retrieved.",
                categoryService.getCategoriesOfCurrentUserByTransactionType(transactionTypeId), HttpStatus.OK);
    }

    @PutMapping("/categories/edit")
    public ResponseEntity<ResponseWrapper<CategoryResponseDTO>> editCategory(@Valid @RequestBody CategoryEditRequestDTO requestDTO){
        return ResponseWrapper.wrap("Category edited.", categoryService.editCategory(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{cat_id}/delete")
    public ResponseEntity<String> deleteCategoryById(@PathVariable("cat_id") int id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().body("Category deleted successfully.");
    }

}
