package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetCreateRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetEditRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.ClosedBudget;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IntervalRepository intervalRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public BudgetResponseDTO createBudget(BudgetCreateRequestDTO requestDTO){
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        if (budgetRepository.existsByAccount_AccountIdAndName(requestDTO.getAccountId(), requestDTO.getName())){
            throw new BadRequestException("Budget with that name already exists for this account.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getIntervalId() != null) {
            throw new BadRequestException("You must select either end date or interval.");
        }
        if (requestDTO.getEndDate() == null && requestDTO.getIntervalId() == null){
            throw new BadRequestException("You must select either end date or interval.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Budget budget = modelMapper.map(requestDTO, Budget.class);
        if (requestDTO.getIntervalId() != null) {
            budget.setInterval(intervalRepository.findById(requestDTO.getIntervalId())
                    .orElseThrow(() -> {throw new BadRequestException("Invalid interval id.");}));
        } else {
            if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
                throw new BadRequestException("Start date cannot be past end date.");
            }
        }
        budget.setAccount(account);
        Set<Category> chosenCategories = categoryRepository.findCategoriesByCategoryIdIn(requestDTO.getCategoryIds());
        validateAndAssignCategories(budget, chosenCategories);
        budget.setAmountSpent(new BigDecimal(0));
        budgetRepository.save(budget);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToBudgetResponseDTO(modelMapper, budget);
    }

    public List<BudgetResponseDTO> getAllBudgetsOfCurrentUser(){
        int userId = MyUserDetailsService.getCurrentUserId();
        List<Budget> budgets = budgetRepository.findAllByAccount_User_UserId(userId);
        return budgets.stream()
                .map(budget -> convertToBudgetResponseDTO(modelMapper, budget))
                .collect(Collectors.toList());
    }

    public BudgetResponseDTO getBudgetById(int budgetId){
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid budget id.");});
        if (budget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this budget.");
        }
        return convertToBudgetResponseDTO(modelMapper, budget);
    }

    public BudgetResponseDTO editBudget(BudgetEditRequestDTO requestDTO) {
        Budget budget = budgetRepository.findById(requestDTO.getBudgetId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid budget id.");});
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        if (budget.getAccount().getAccountId() != requestDTO.getAccountId()) {
            throw new ForbiddenException("You cannot change account id.");
        }

        if (!budget.getName().equals(requestDTO.getName())) {
            if (budgetRepository.existsByAccount_AccountIdAndName(requestDTO.getAccountId(), requestDTO.getName())) {
                throw new BadRequestException("A budget with that name already exists.");
            }
            budget.setName(requestDTO.getName());
        }
        if (budget.getInterval() != null  && requestDTO.getEndDate() != null) {
            throw new BadRequestException("You can't select an end date for non-one-time budgets.");
        }
        if (budget.getInterval() == null &&  requestDTO.getEndDate() == null) {
            throw new BadRequestException("You cannot have a budget with no end date and no interval.");
        }
        if (requestDTO.getEndDate() != null && budget.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        budget.setMaxLimit(requestDTO.getMaxLimit());
        budget.setNote(requestDTO.getNote());
        budget.setEndDate(requestDTO.getEndDate());
        Set<Category> chosenCategories = categoryRepository.findCategoriesByCategoryIdIn(requestDTO.getCategoryIds());
        validateAndAssignCategories(budget, chosenCategories);
        budgetRepository.save(budget);
        return convertToBudgetResponseDTO(modelMapper, budget);
    }

    public void deleteBudgetById(int budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> {throw new NotFoundException("Budget does not exist.");});
        if (budget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this budget.");
        }
        budgetRepository.deleteById(budgetId);
    }

    @Transactional
    public ClosedBudgetResponseDTO closeBudgetById(int budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid budget id.");});
        if (budget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this budget.");
        }
        ClosedBudget closedBudget = modelMapper.map(budget, ClosedBudget.class);
        closedBudget.setClosedBudgetId(0);
        closedBudget.setClosedBudgetCategories(budget.getCategories());
        budgetRepository.deleteById(budgetId);
        closedBudgetRepository.save(closedBudget);
        return ClosedBudgetService.convertToClosedBudgetResponseDTO(modelMapper, closedBudget);
    }

    private void validateAndAssignCategories(Budget budget, Set<Category> categories){
        if  (categories.isEmpty()){
            throw new BadRequestException("Invalid category ids.");
        }
        budget.setCategories(categories);
        budget.getCategories().forEach(category -> {
            if (category.getUser() != null && budget.getAccount().getUser().getUserId() != category.getUser().getUserId()) {
                //todo LOG OUT hacker
                throw new ForbiddenException("You don't have access to this category.");
            }
            if (category.getTransactionType().getName().equalsIgnoreCase("income")) {
                throw new BadRequestException("You cannot add income categories to budgets.");
            }
        });
    }

    static BudgetResponseDTO convertToBudgetResponseDTO(@Autowired ModelMapper modelMapper, Budget budget) {
        BudgetResponseDTO responseDTO = modelMapper.map(budget, BudgetResponseDTO.class);
        responseDTO.setCategoryResponseDTOs(budget.getCategories().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toSet()));
        responseDTO.setCurrency(budget.getAccount().getCurrency());
        return responseDTO;
    }
}
