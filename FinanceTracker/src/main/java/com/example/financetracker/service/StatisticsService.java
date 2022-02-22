package com.example.financetracker.service;

import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.TopFiveExpensesOrIncomesResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByDateAndFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class StatisticsService {

    @Autowired
    private StatisticsDAO statisticsDAO;

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        return statisticsDAO.getBudgetsByFilters(filtersDTO);
    }

    public List<RecurrentTransactionResponseDTO> getRecurrentTransactionsByFilters(RecurrentTransactionByFiltersDTO filtersDTO) {
        return statisticsDAO.getRecurrentTransactionsByFilters(filtersDTO);
    }

    public List<TransactionResponseDTO> getTransactionsByFilters(TransactionByDateAndFiltersRequestDTO requestDTO) {
        return statisticsDAO.getTransactionsByFilters(requestDTO);
    }

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveExpensesByDates(FilterByDatesRequestDTO requestDTO) {
        return statisticsDAO.getTopFiveExpensesOrIncomesByDates(requestDTO, "expense");
    }
    
    public TopFiveExpensesOrIncomesResponseDTO getTopFiveIncomesByDates(FilterByDatesRequestDTO requestDTO) {
        return statisticsDAO.getTopFiveExpensesOrIncomesByDates(requestDTO, "income");
    }

}
