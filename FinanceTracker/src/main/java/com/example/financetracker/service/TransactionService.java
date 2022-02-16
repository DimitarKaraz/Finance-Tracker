package com.example.financetracker.service;

import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.CategoryRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import com.example.financetracker.model.repositories.TransactionTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO requestDTO){
        //todo validations
        Transaction transaction = modelMapper.map(requestDTO, Transaction.class);
        transaction.setDateTime(LocalDateTime.now());
        Set<Budget> affectedBudgets = budgetRepository.findAllBudgetsByCategoryAndAccount(requestDTO.getAccountId(), requestDTO.getCategoryId());
        for (Budget budget : affectedBudgets){
            budget.setAmountSpent(budget.getAmountSpent().add(transaction.getAmount()));
            //todo if budget is 75% spent or 100% spent send notifications/warnings to user
            budgetRepository.save(budget);
        }
        transaction.setTransactionType();
        transactionRepository.save(transaction);
        return modelMapper.map(transaction, TransactionResponseDTO.class);
    }


}
