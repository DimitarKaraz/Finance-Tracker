package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.*;
import com.example.financetracker.model.dto.specialStatisticsDTOs.AverageTransactionForTransactionTypesResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.TopFiveExpensesOrIncomesResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.NumberOfTransactionsByTypeResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PutMapping("/budgets/stats")
    public ResponseEntity<ResponseWrapper<List<BudgetResponseDTO>>> getBudgetsByFilters(@Valid @RequestBody BudgetByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Budgets by filters retrieved.",
                statisticsService.getBudgetsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/closed_budgets/stats")
    public ResponseEntity<ResponseWrapper<List<ClosedBudgetResponseDTO>>> getClosedBudgetsByFilters(@Valid @RequestBody BudgetByFiltersRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Closed budgets by filters retrieved.",
                statisticsService.getClosedBudgetsByFilters(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/recurrent_transactions/stats")
    public ResponseEntity<ResponseWrapper<LinkedHashMap<String, Object>>> getRecurrentTransactionsByFilters
            (@Valid @RequestBody RecurrentTransactionByFiltersRequestDTO requestDTO, @RequestParam(name = "page", defaultValue = "0") int pageNumber) {
        return ResponseWrapper.wrap("Recurrent Transactions by filters retrieved.",
                statisticsService.getRecurrentTransactionsByFilters(requestDTO, pageNumber), HttpStatus.OK);
    }

    @PutMapping("/transactions/stats")
    public ResponseEntity<ResponseWrapper<LinkedHashMap<String, Object>>> getTransactionsByFilters
            (@Valid @RequestBody TransactionByFiltersRequestDTO requestDTO, @RequestParam(name = "page", defaultValue = "0") int pageNumber) {
        return ResponseWrapper.wrap("Transactions by filters retrieved.",
                statisticsService.getTransactionsByFilters(requestDTO, pageNumber), HttpStatus.OK);
    }

    @PutMapping("/categories/top-5-expenses")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveExpensesCategories(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five expenses by category retrieved.",
                statisticsService.getTopFiveExpensesCategories(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/categories/top-5-incomes")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveIncomesCategories(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five incomes by category retrieved.",
                statisticsService.getTopFiveIncomesCategories(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/payment_methods/top-5-expenses")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveExpensesPaymentMethods(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five expenses by payment method retrieved.",
                statisticsService.getTopFiveExpensesPaymentMethods(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/payment_methods/top-5-incomes")
    public ResponseEntity<ResponseWrapper<TopFiveExpensesOrIncomesResponseDTO>> getTopFiveIncomesPaymentMethods(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Top five incomes by payment method retrieved.",
                statisticsService.getTopFiveIncomesPaymentMethods(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/accounts/cash-flows")
    public ResponseEntity<ResponseWrapper<CashFlowsResponseDTO>> getCashFlowsForAccounts(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Cash-flows for accounts retrieved.",
                statisticsService.getCashFlowsForAccounts(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/transactions/average_for_transaction_types")
    public ResponseEntity<ResponseWrapper<AverageTransactionForTransactionTypesResponseDTO>> getAverageTransactions(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Average transaction amount for transaction types retrieved.",
                statisticsService.getAverageTransactions(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/transactions/count_for_transaction_types")
    public ResponseEntity<ResponseWrapper<NumberOfTransactionsByTypeResponseDTO>> getNumberOfTransactionsByType(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Transaction count for transaction types retrieved.",
                statisticsService.getNumberOfTransactionsByType(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/transactions/sum_for_transaction_types")
    public ResponseEntity<ResponseWrapper<SumOfTransactionsByTypeResponseDTO>> getSumOfTransactionsByType(@Valid @RequestBody FilterByDatesRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Transaction sum for transaction types retrieved.",
                statisticsService.getSumOfTransactionsByType(requestDTO), HttpStatus.OK);
    }


}
