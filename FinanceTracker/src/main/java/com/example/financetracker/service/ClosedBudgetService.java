package com.example.financetracker.service;

import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.ClosedBudget;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.ClosedBudgetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ClosedBudgetService {

    @Autowired
    private BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Transactional
    public BudgetResponseDTO openClosedBudgetById(int closedBudgetId) {
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                        .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        Budget budget = modelMapper.map(closedBudget, Budget.class);

        closedBudgetRepository.deleteById(closedBudgetId);
        budgetRepository.save(budget);
        return budgetService.convertToResponseDTO(budget);
    }



    public void deleteClosedBudget(int closedBudgetId) {
        if (!budgetRepository.existsById(closedBudgetId)) {
            throw new NotFoundException("Closed budget does not exist.");
        }
        budgetRepository.deleteById(closedBudgetId);
    }


}
