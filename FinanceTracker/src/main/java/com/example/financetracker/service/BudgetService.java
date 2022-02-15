package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.*;
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
    @Autowired
    private CategoryRepository categoryRepository;

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
        budget.setLimit(requestDTO.getLimit());
        budget.setAmountSpent(new BigDecimal(0));
        //TODO: DAO
        budget.setCategories(requestDTO.getCategoryIds().stream()
                .map(integer -> categoryRepository.findById(integer).orElse(null))
                .collect(Collectors.toSet()));
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
