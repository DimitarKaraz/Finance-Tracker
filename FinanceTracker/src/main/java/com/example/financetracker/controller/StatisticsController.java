package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PutMapping("/budgets/stats")
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getBudgetsByFilters(@RequestBody BudgetByFiltersDTO filtersDTO) {
        return ResponseWrapper.wrap("Budgets by filters retrieved.",
                statisticsService.getBudgetsByFilters(filtersDTO), HttpStatus.OK);
    }


}
