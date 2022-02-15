package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> createBudget(@Valid @RequestBody BudgetCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Budget created successfully.", budgetService.createBudget(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("{user_id}/all")
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getAllForUser(@PathVariable("user_id") int id){
        return ResponseWrapper.wrap("Budgets retrieved.", budgetService.getAllBudgetsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{budget_id}")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> getBudgetById(@PathVariable("budget_id") int budgetId){
        return ResponseWrapper.wrap("Budget retrieved.", budgetService.getBudgetById(budgetId), HttpStatus.OK);
    }

}
