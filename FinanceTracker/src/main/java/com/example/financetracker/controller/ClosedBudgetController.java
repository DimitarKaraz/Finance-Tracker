package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.service.ClosedBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ClosedBudgetController {

    @Autowired
    private ClosedBudgetService closedBudgetService;

    @PostMapping("/closed_budgets/{cb_id}/open")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> openClosedBudgetById(@PathVariable("cb_id") int id){
        return ResponseWrapper.wrap("Budget opened successfully.",
                closedBudgetService.openClosedBudgetById(id), HttpStatus.CREATED);
    }

    @GetMapping("/closed_budgets")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllClosedBudgetsOfCurrentUser(
            @RequestParam(name = "page", defaultValue = "0") int pageNo) {
        return ResponseWrapper.wrap("ClosedBudgets retrieved.",
                closedBudgetService.getAllClosedBudgetsOfCurrentUser(pageNo), HttpStatus.OK);
    }

    @GetMapping("/closed_budgets/{cb_id}")
    public ResponseEntity<ResponseWrapper<ClosedBudgetResponseDTO>> getClosedBudgetById(@PathVariable("cb_id") int ClosedBudgetId){
        return ResponseWrapper.wrap("ClosedBudget retrieved.",
                closedBudgetService.getClosedBudgetById(ClosedBudgetId), HttpStatus.OK);
    }

    @DeleteMapping("/closed_budgets/{cb_id}/delete")
    public ResponseEntity<String> deleteClosedBudgetById(@PathVariable("cb_id") int id) {
        closedBudgetService.deleteClosedBudgetById(id);
        return ResponseEntity.ok().body("ClosedBudget deleted successfully.");
    }

}
