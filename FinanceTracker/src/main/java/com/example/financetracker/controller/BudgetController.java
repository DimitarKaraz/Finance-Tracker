package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;


















    @PutMapping("/edit_budget")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> editBudget(@RequestBody BudgetEditRequestDTO requestDTO) {
        //TODO: SECURITY -> only for users with the same id
        return ResponseWrapper.wrap("Account edited.", budgetService.editBudget(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete_budget/{budget_id}")
    public ResponseEntity<String> deleteBudget(@PathVariable("budget_id") int id) {
        //TODO: SECURITY -> only for user with same id
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().body("Budget deleted successfully.");
    }
}
