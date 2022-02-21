package com.example.financetracker.service;

import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
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
}
