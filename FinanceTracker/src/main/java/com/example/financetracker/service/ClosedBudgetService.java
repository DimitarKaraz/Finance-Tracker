package com.example.financetracker.service;

import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.ClosedBudget;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.ClosedBudgetRepository;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClosedBudgetService {

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
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        Budget budget = modelMapper.map(closedBudget, Budget.class);
        budget.setBudgetId(0);
        budget.setStartDate(LocalDate.now());
        budget.setCategories(closedBudget.getClosedBudgetCategories());
        closedBudgetRepository.deleteById(closedBudgetId);
        budgetRepository.save(budget);
        return BudgetService.convertToBudgetResponseDTO(modelMapper, budget);
    }

    public List<ClosedBudgetResponseDTO> getAllClosedBudgetsOfCurrentUser(){
        int userId = MyUserDetailsService.getCurrentUserId();
        List<ClosedBudget> closedBudgets = closedBudgetRepository.findAllByAccount_User_UserId(userId);
        return closedBudgets.stream()
                .map(closedBudget -> convertToClosedBudgetResponseDTO(modelMapper, closedBudget))
                .collect(Collectors.toList());
    }

    public ClosedBudgetResponseDTO getClosedBudgetById(int closedBudgetId){
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        return convertToClosedBudgetResponseDTO(modelMapper, closedBudget);
    }

    public void deleteClosedBudgetById(int closedBudgetId) {
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        closedBudgetRepository.deleteById(closedBudgetId);
    }

    static ClosedBudgetResponseDTO convertToClosedBudgetResponseDTO(@Autowired ModelMapper modelMapper, ClosedBudget closedBudget) {
        ClosedBudgetResponseDTO responseDTO = modelMapper.map(closedBudget, ClosedBudgetResponseDTO.class);
        responseDTO.setCategoryResponseDTOs(closedBudget.getClosedBudgetCategories().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toSet()));
        responseDTO.setCurrency(closedBudget.getAccount().getCurrency());
        return responseDTO;
    }


}
