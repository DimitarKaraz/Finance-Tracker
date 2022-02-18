package com.example.financetracker.service;

import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.ClosedBudget;
import com.example.financetracker.model.repositories.ClosedBudgetRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClosedBudgetService {

    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<ClosedBudgetResponseDTO> getAllClosedBudgetsByUserId(int userId){
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Invalid user id.");
        }
        List<ClosedBudget> closedBudgets = closedBudgetRepository.findAllByAccount_User_UserId(userId);
        return closedBudgets.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public ClosedBudgetResponseDTO getClosedBudgetById(int closedBudgetId){
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        //TODO: SECURITY:
//        if (budget.getAccount().getUser().getUserId() != <user session id>) {
//            throw new UnauthorizedException("You must be logged in.");
//        }
        return convertToResponseDTO(closedBudget);
    }

    public ClosedBudgetResponseDTO convertToResponseDTO(ClosedBudget closedBudget) {
        ClosedBudgetResponseDTO responseDTO = modelMapper.map(closedBudget, ClosedBudgetResponseDTO.class);
        responseDTO.setCategoryResponseDTOs(closedBudget.getClosedBudgetCategories().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toSet()));
        responseDTO.setCurrency(closedBudget.getAccount().getCurrency());
        return responseDTO;
    }

}
