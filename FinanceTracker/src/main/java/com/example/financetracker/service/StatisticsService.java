package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dao.SpecialStatisticsDAO;
import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.*;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
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
    @Autowired
    private AccountRepository accountRepository;

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        validateAccountId(requestDTO.getAccountId());
        return statisticsDAO.getBudgetsByFilters(requestDTO);
    }

    public List<RecurrentTransactionResponseDTO> getRecurrentTransactionsByFilters(RecurrentTransactionByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        validateAccountId(requestDTO.getAccountId());
        return statisticsDAO.getRecurrentTransactionsByFilters(requestDTO);
    }

    public List<TransactionResponseDTO> getTransactionsByFilters(TransactionByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        validateAccountId(requestDTO.getAccountId());
        return statisticsDAO.getTransactionsByFilters(requestDTO);
    }

    public List<ClosedBudgetResponseDTO> getClosedBudgetsByFilters(BudgetByFiltersRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        validateAccountId(requestDTO.getAccountId());
        return statisticsDAO.getClosedBudgetsByFilters(requestDTO);
    }

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveExpensesCategories(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesCategories(requestDTO, "expense");
    }
    
    public TopFiveExpensesOrIncomesResponseDTO getTopFiveIncomesCategories(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesCategories(requestDTO, "income");
    }

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveExpensesPaymentMethods(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesPaymentMethods(requestDTO, "expense");
    }

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveIncomesPaymentMethods(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getTopFiveExpensesOrIncomesPaymentMethods(requestDTO, "income");
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

    public NumberOfTransactionsByTypeResponseDTO getNumberOfTransactionsByType(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getNumberOfTransactionsByTransactionTypes(requestDTO);
    }

    public SumOfTransactionsByTypeResponseDTO getSumOfTransactionsByType(FilterByDatesRequestDTO requestDTO) {
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        return specialStatisticsDAO.getSumOfTransactionsByTransactionTypes(requestDTO);
    }

    private void validateAccountId(Integer accountId) {
        if (accountId == null){
            return;
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
    }
    
}
