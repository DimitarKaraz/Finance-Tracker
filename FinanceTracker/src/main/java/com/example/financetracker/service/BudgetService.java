package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.IntervalRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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








































    public BudgetResponseDTO editBudget(BudgetEditRequestDTO requestDTO) {
        if (budgetRepository.existsById(requestDTO.getBudgetId())) {
            throw new BadRequestException("Invalid budget id.");
        }
        Account account = accountRepository.findById(requestDTO.getAccountId()).orElse(null);
        if (account == null) {
            throw new BadRequestException("Invalid account id.");
        }
        //TODO: SECURITY -> only for users with same id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new BadRequestException("Invalid user id.");
        }
        Budget budget = modelMapper.map(requestDTO, Budget.class);
        if (!budget.getName().equals(requestDTO.getName())) {
            if (budgetRepository.existsByAccount_AccountIdAndName(requestDTO.getAccountId(), requestDTO.getName())) {
                throw new BadRequestException("A budget with that name already exists.");
            }
        }
        budget.setName(requestDTO.getName());
        budget.setLimit(requestDTO.getLimit());
        budget.setNote(requestDTO.getNote());
        budget.setCategories(requestDTO.getCategories());
        budgetRepository.save(budget);
        return modelMapper.map(budget, BudgetResponseDTO.class);
    }


    public void deleteBudget(int id) {
        if (!budgetRepository.existsById(id)) {
            throw new NotFoundException("Budget does not exist.");
        }
        budgetRepository.deleteById(id);
    }
}
