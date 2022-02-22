package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dao.SpecialStatisticsDAO;
import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.AverageTransactionForTransactionTypesResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.TopFiveExpensesOrIncomesResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class StatisticsService {

    @Autowired
    private StatisticsDAO statisticsDAO;
    @Autowired
    private SpecialStatisticsDAO specialStatisticsDAO;

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return statisticsDAO.getBudgetsByFilters(requestDTO);
    }

    public List<RecurrentTransactionResponseDTO> getRecurrentTransactionsByFilters(RecurrentTransactionByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return statisticsDAO.getRecurrentTransactionsByFilters(requestDTO);
    }

    public List<TransactionResponseDTO> getTransactionsByFilters(TransactionByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return statisticsDAO.getTransactionsByFilters(requestDTO);
    }

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveExpensesByDates(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesByDates(requestDTO, "expense");
    }
    
    public TopFiveExpensesOrIncomesResponseDTO getTopFiveIncomesByDates(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesByDates(requestDTO, "income");
    }

    public CashFlowsResponseDTO getCashFlowsForAccounts(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getCashFlowsForAccounts(requestDTO);
    }


    public AverageTransactionForTransactionTypesResponseDTO getAverageTransactions(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getAverageTransactions(requestDTO);
    }
}
