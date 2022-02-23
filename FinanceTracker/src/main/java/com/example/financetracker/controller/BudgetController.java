package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/budgets/create")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> createBudget(@Valid @RequestBody BudgetCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Budget created successfully.",
                budgetService.createBudget(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/budgets")
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getAllBudgetsOfCurrentUser(){
        return ResponseWrapper.wrap("Budgets retrieved.",
                budgetService.getAllBudgetsOfCurrentUser(), HttpStatus.OK);
    }

    @GetMapping("/budgets/{budget_id}")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> getBudgetById(@PathVariable("budget_id") int budgetId){
        return ResponseWrapper.wrap("Budget retrieved.",
                budgetService.getBudgetById(budgetId), HttpStatus.OK);
    }

    @PutMapping("/budgets/edit")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> editBudget(@Valid @RequestBody BudgetEditRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Budget edited.",
                budgetService.editBudget(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/budgets/{budget_id}/delete")
    public ResponseEntity<String> deleteBudgetById(@PathVariable("budget_id") int id) {
        budgetService.deleteBudgetById(id);
        return ResponseEntity.ok().body("Budget deleted successfully.");
    }

    @PostMapping("/budgets/{budget_id}/close")
    public ResponseEntity<ResponseWrapper<ClosedBudgetResponseDTO>> closeBudgetById(@PathVariable("budget_id") int id) {
        return ResponseWrapper.wrap("Budget was closed successfully.",
                budgetService.closeBudgetById(id), HttpStatus.OK);
    }


}
