package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
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

    @PutMapping("/recurrent_transactions/stats")
    public ResponseEntity<ResponseWrapper<List<RecurrentTransactionResponseDTO>>> getBudgetsByFilters(@RequestBody RecurrentTransactionByFiltersDTO filtersDTO) {
        return ResponseWrapper.wrap("Budgets by filters retrieved.",
                statisticsService.getRecurrentTransactionsByFilters(filtersDTO), HttpStatus.OK);
    }

    @PutMapping("accounts/cash-flows")
    public ResponseEntity<ResponseWrapper<CashFlowsResponseDTO>> getCashFlowsForAccounts(@RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Cash-flows for accounts retrieved.",
                statisticsService.getCashFlowsForAccounts(requestDTO), HttpStatus.OK);
    }


}
