package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
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

        if (!accountRepository.existsById(requestDTO.getAccountId())){
            throw new BadRequestException("Invalid account.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getIntervalId() != null ||
                requestDTO.getEndDate() == null && requestDTO.getIntervalId() == null) {
            throw new BadRequestException("You must select either end date or interval.");
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Budget budget = modelMapper.map(requestDTO, Budget.class);

        if (requestDTO.getIntervalId() != null) {
            if (!intervalRepository.existsById(requestDTO.getIntervalId())){
                throw new BadRequestException("Invalid interval.");
            }
            budget.setInterval(intervalRepository.findById(requestDTO.getIntervalId()).orElse(null));
        } else {
            if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
                throw new BadRequestException("Start date cannot be past end date.");
            }
        }
        //TODO: security -> check if account.findById(requestDTO.getAccountId).getUser.getUserId == session user_id
        budget.setAccount(accountRepository.findById(requestDTO.getAccountId()).orElse(null));
        budget.setEndDate(requestDTO.getEndDate());
        budget.setMaxLimit(requestDTO.getMaxLimit());
        budget.setAmountSpent(new BigDecimal(0));
        budget.setCategories(categoryRepository.findCategoriesByCategoryIdIn(requestDTO.getCategoryIds()));
        budgetRepository.save(budget);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToResponseDTO(budget);
    }

    public List<BudgetResponseDTO> getAllBudgetsByUserId(int userId){
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Invalid user id.");
        }
        List<Budget> budgets = budgetRepository.findAllByAccount_User_UserId(userId);
        return budgets.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public BudgetResponseDTO getBudgetById(int budgetId){
        Budget budget = budgetRepository.findById(budgetId).orElse(null);
        if (budget == null){
            throw new NotFoundException("Invalid budget id.");
        }
        //TODO: SECURITY:
//        if (budget.getAccount().getUser().getUserId() != <user session id>) {
//            throw new UnauthorizedException("You must be logged in.");
//        }
        return convertToResponseDTO(budget);
    }

    public BudgetResponseDTO editBudget(BudgetEditRequestDTO requestDTO) {
        Budget budget = budgetRepository.findById(requestDTO.getBudgetId()).orElse(null);

        if (budget == null) {
            throw new NotFoundException("Invalid budget id.");
        }
        Account account = accountRepository.findById(requestDTO.getAccountId()).orElse(null);
        if (account == null) {
            throw new BadRequestException("Invalid account id.");
        }
        if (budget.getAccount().getAccountId() != requestDTO.getAccountId()) {
            throw new UnauthorizedException("You don't have permission to edit this budget.");
        }
        //TODO: SECURITY -> only for users with same id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new NotFoundException("Invalid user id.");
        }
        if (!budget.getName().equals(requestDTO.getName())) {
            if (budgetRepository.existsByAccount_AccountIdAndName(requestDTO.getAccountId(), requestDTO.getName())) {
                throw new BadRequestException("A budget with that name already exists.");
            }
        }
        if (budget.getInterval() != null  && requestDTO.getEndDate() != null) {
            throw new BadRequestException("You can't select an end date for non-one-time budgets.");
        }
        if (requestDTO.getEndDate() == null && budget.getInterval() == null) {
            throw new BadRequestException("You cannot have a budget with no end date and no interval.");
        }
        if (requestDTO.getEndDate() != null && budget.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        budget.setName(requestDTO.getName());
        budget.setMaxLimit(requestDTO.getMaxLimit());
        budget.setNote(requestDTO.getNote());
        budget.setEndDate(requestDTO.getEndDate());
        budget.setCategories(categoryRepository.findCategoriesByCategoryIdIn(requestDTO.getCategoryIds()));
        budgetRepository.save(budget);
        return convertToResponseDTO(budget);
    }

    public void deleteBudget(int id) {
        if (!budgetRepository.existsById(id)) {
            throw new NotFoundException("Budget does not exist.");
        }
        budgetRepository.deleteById(id);
    }

    private BudgetResponseDTO convertToResponseDTO(Budget budget) {
        BudgetResponseDTO responseDTO = modelMapper.map(budget, BudgetResponseDTO.class);
        responseDTO.setCategoryResponseDTOs(budget.getCategories().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toSet()));
        responseDTO.setCurrency(budget.getAccount().getCurrency());
        return responseDTO;
    }

}
