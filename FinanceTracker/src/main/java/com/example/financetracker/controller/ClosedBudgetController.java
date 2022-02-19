package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.service.ClosedBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/closed_budgets")
public class ClosedBudgetController {

    @Autowired
    private ClosedBudgetService closedBudgetService;

    @PostMapping("/{cl_bu_id}/open")
    public ResponseEntity<ResponseWrapper<BudgetResponseDTO>> openClosedBudgetById(@PathVariable("cl_bu_id") int id){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Budget opened successfully.", closedBudgetService.openClosedBudgetById(id), HttpStatus.CREATED);
    }

    @GetMapping("{user_id}/all")
    public ResponseEntity<ResponseWrapper<List<ClosedBudgetResponseDTO>>> getAllClosedBudgetsByUserId(@PathVariable("user_id") int id){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("ClosedBudgets retrieved.", closedBudgetService.getAllClosedBudgetsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{ClosedBudget_id}")
    public ResponseEntity<ResponseWrapper<ClosedBudgetResponseDTO>> getClosedBudgetById(@PathVariable("ClosedBudget_id") int ClosedBudgetId){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("ClosedBudget retrieved.", closedBudgetService.getClosedBudgetById(ClosedBudgetId), HttpStatus.OK);
    }


    @DeleteMapping("/{ClosedBudget_id}/delete_closed_budget")
    public ResponseEntity<String> deleteClosedBudgetById(@PathVariable("ClosedBudget_id") int id) {
        //TODO: SECURITY -> only for user with same id
        closedBudgetService.deleteClosedBudget(id);
        return ResponseEntity.ok().body("ClosedBudget deleted successfully.");
    }

}