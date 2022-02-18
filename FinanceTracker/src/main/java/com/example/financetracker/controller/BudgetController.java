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
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> createBudget(@Valid @RequestBody BudgetCreateRequestDTO requestDTO){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Budget created successfully.", budgetService.createBudget(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("{user_id}/all")
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getAllBudgetsByUserId(@PathVariable("user_id") int id){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Budgets retrieved.", budgetService.getAllBudgetsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{budget_id}")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> getBudgetById(@PathVariable("budget_id") int budgetId){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Budget retrieved.", budgetService.getBudgetById(budgetId), HttpStatus.OK);
    }

    @PutMapping("/edit_budget")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> editBudget(@Valid @RequestBody BudgetEditRequestDTO requestDTO) {
        //TODO: SECURITY -> only for users with the same id
        return ResponseWrapper.wrap("Budget edited.", budgetService.editBudget(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{budget_id}/delete_budget")
    public ResponseEntity<String> deleteBudgetById(@PathVariable("budget_id") int id) {
        //TODO: SECURITY -> only for user with same id
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().body("Budget deleted successfully.");
    }

    @PostMapping("/{budget_id}/close_budget")
    public ResponseEntity<ResponseWrapper<ClosedBudgetResponseDTO>> closeBudgetById(@PathVariable("budget_id") int id) {
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Budget was close successfully.", budgetService.closeBudgetById(id), HttpStatus.OK);
    }


}
