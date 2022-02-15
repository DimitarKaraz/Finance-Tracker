package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryEditRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{user_id}/categories")
    public ResponseEntity<ResponseWrapper<List<Category>>> getAllCategoriesByUserId(@PathVariable("user_id") int id){
        //TODO: SECURITY -> only for user with the same id
        return ResponseWrapper.wrap("Categories for user " + id + " retrieved.",
                categoryService.getAllCategoriesByUserId(id), HttpStatus.OK);
    }

    @PostMapping("/create_category")
    public ResponseEntity<ResponseWrapper<CategoryResponseDTO>> createCategory(@RequestBody CategoryCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Category created.", categoryService.createCategory(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/edit_category")
    public ResponseEntity<ResponseWrapper<CategoryResponseDTO>> editCategory(@RequestBody CategoryEditRequestDTO requestDTO){
        return ResponseWrapper.wrap("Category edited.", categoryService.editCategory(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete_category")
    public ResponseEntity<String> deleteCategory(@RequestBody CategoryEditRequestDTO requestDTO){
        categoryService.deleteCategory(requestDTO);
        return ResponseEntity.ok().body("Category deleted.");
    }

}
