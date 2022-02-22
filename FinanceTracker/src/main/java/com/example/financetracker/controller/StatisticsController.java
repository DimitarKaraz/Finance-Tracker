package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.TopFiveExpensesOrIncomesResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.NumberOfTransactionsByTypeResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
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
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getBudgetsByFilters(@RequestBody BudgetByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Budgets by filters retrieved.",
                statisticsService.getBudgetsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/closed_budgets/stats")
    public ResponseEntity<ResponseWrapper<List<ClosedBudgetResponseDTO>>> getClosedBudgetsByFilters(@RequestBody BudgetByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Closed budgets by filters retrieved.",
                statisticsService.getClosedBudgetsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/recurrent_transactions/stats")
    public ResponseEntity<ResponseWrapper<List<RecurrentTransactionResponseDTO>>> getBudgetsByFilters(@RequestBody RecurrentTransactionByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Budgets by filters retrieved.",
                statisticsService.getRecurrentTransactionsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/transactions/stats")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getTransactionsByFilters(@RequestBody TransactionByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Transactions by filters retrieved.",
                statisticsService.getTransactionsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/categories/top-5-expenses")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveExpensesByDates(@RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five expenses retrieved.",
                statisticsService.getTopFiveExpensesByDates(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/categories/top-5-incomes")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveIncomesByDates(@RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five incomes retrieved.",
                statisticsService.getTopFiveIncomesByDates(requestDTO), HttpStatus.OK);
    }

    @PutMapping("accounts/cash-flows")
    public ResponseEntity<ResponseWrapper<CashFlowsResponseDTO>> getCashFlowsForAccounts(@RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Cash-flows for accounts retrieved.",
                statisticsService.getCashFlowsForAccounts(requestDTO), HttpStatus.OK);

    }

    @PutMapping("/transactions_by_type/count")
    public ResponseEntity<ResponseWrapper<NumberOfTransactionsByTypeResponseDTO>> getNumberOfTransactionsByType(@RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Transaction count by type retrieved.",
                statisticsService.getNumberOfTransactionsByType(requestDTO), HttpStatus.OK);
    }


}
