package com.example.financetracker.service;

import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.BudgetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsDAO statisticsDAO;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        List<Budget> budgets = statisticsDAO.getBudgetsByFilters(filtersDTO);
        return budgets.stream()
                .map(budget -> BudgetService.convertToBudgetResponseDTO(modelMapper, budget))
                .collect(Collectors.toList());
    }
}
