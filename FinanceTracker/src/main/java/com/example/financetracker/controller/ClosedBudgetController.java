package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.service.BudgetService;
import com.example.financetracker.service.ClosedBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/closed_budgets")
public class ClosedBudgetController {

    @Autowired
    private BudgetService budgetService;
    @Autowired
    private ClosedBudgetService closedBudgetService;

    @PostMapping("/{cl_bu_id}/open")
    public ResponseEntity<ResponseWrapper<Budget>> openClosedBudgetById(@PathVariable("cl_bu_id") int id){
        return ResponseWrapper.wrap("Budget opened successfully.", closedBudgetService.openClosedBudgetById(id), HttpStatus.CREATED);
    }

    @GetMapping("{user_id}/all")
    public ResponseEntity<ResponseWrapper<List<ClosedBudgetResponseDTO>>> getAllClosedBudgetsByUserId(@PathVariable("user_id") int id){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("ClosedBudgets retrieved.", ClosedBudgetService.getAllClosedBudgetsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{ClosedBudget_id}")
    public ResponseEntity<ResponseWrapper<ClosedBudgetResponseDTO>> getClosedBudgetById(@PathVariable("ClosedBudget_id") int ClosedBudgetId){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("ClosedBudget retrieved.", ClosedBudgetService.getClosedBudgetById(ClosedBudgetId), HttpStatus.OK);
    }


    @DeleteMapping("/{ClosedBudget_id}/delete_ClosedBudget")
    public ResponseEntity<String> deleteClosedBudgetById(@PathVariable("ClosedBudget_id") int id) {
        //TODO: SECURITY -> only for user with same id
        ClosedBudgetService.deleteClosedBudget(id);
        return ResponseEntity.ok().body("ClosedBudget deleted successfully.");
    }

}
