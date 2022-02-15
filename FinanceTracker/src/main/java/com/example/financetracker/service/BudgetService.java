package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.IntervalRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IntervalRepository intervalRepository;


    @Transactional
    public BudgetResponseDTO createBudget(BudgetCreateRequestDTO requestDTO){
        if (budgetRepository.existsByAccount_AccountIdAndName(requestDTO.getAccountId(), requestDTO.getName())){
            throw new BadRequestException("Budget with that name already exists for this account.");
        }
        if (!intervalRepository.existsById(requestDTO.getIntervalId())){
            throw new BadRequestException("Invalid interval.");
        }
        if (!accountRepository.existsById(requestDTO.getAccountId())){
            throw new BadRequestException("Invalid account.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Budget budget = modelMapper.map(requestDTO, Budget.class);
        budget.setInterval(intervalRepository.findById(requestDTO.getIntervalId()).orElse(null));
        budget.setAccount(accountRepository.findById(requestDTO.getAccountId()).orElse(null));
        budget.setAmountSpent(new BigDecimal(0));
        budgetRepository.save(budget);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(budget, BudgetResponseDTO.class);
    }

    public List<BudgetResponseDTO> getAllBudgetsByUserId(int userId){
        List<Budget> budgets = budgetRepository.findAllByAccount_User_UserId(userId);
        return budgets.stream().map(budget -> modelMapper.map(budget, BudgetResponseDTO.class)).collect(Collectors.toList());
    }

    public BudgetResponseDTO getBudgetById(int budgetId){
        Optional<Budget> budget = budgetRepository.findById(budgetId);
        if (budget.isEmpty()){
            throw new BadRequestException("The budget you are trying to delete does not exist.");
        }
        return modelMapper.map(budget.get(), BudgetResponseDTO.class);
    }

}
