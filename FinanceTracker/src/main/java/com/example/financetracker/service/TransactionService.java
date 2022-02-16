package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Service
public class TransactionService {
    //todo set date_time to now() when creating
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TransactionResponseDTO editTransaction(TransactionEditRequestDTO requestDTO) {
        BigDecimal subtractFromAffectedBudgets = validateEditRequest(requestDTO);

        Transaction transaction = modelMapper.map(requestDTO, Transaction.class);
        System.out.println("\n************** "  + transaction);
        transactionRepository.save(transaction);

        Set<Budget> affectedBudgets = budgetRepository.findAllBudgetsByCategoryAndAccount(requestDTO.getAccountId(), requestDTO.getCategoryId());
        updateAffectedBudgets(subtractFromAffectedBudgets, transaction.getAmount(), affectedBudgets);

        return convertToResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO requestDTO){
        //todo validations
        Transaction transaction = modelMapper.map(requestDTO, Transaction.class);
        transaction.setDateTime(LocalDateTime.now());
        Set<Budget> affectedBudgets = budgetRepository.findAllBudgetsByCategoryAndAccount(requestDTO.getAccountId(), requestDTO.getCategoryId());

        updateAffectedBudgets(new BigDecimal(0), transaction.getAmount(), affectedBudgets);

        transactionRepository.save(transaction);
        return convertToResponseDTO(transaction);
    }

    private void updateAffectedBudgets(BigDecimal oldTransactionAmount, BigDecimal newTransactionAmount, Set<Budget> affectedBudgets) {
        for (Budget budget : affectedBudgets){
            budget.setAmountSpent(budget.getAmountSpent().subtract(oldTransactionAmount));
            budget.setAmountSpent(budget.getAmountSpent().add(newTransactionAmount));
            //todo if budget is 75% spent or 100% spent send notifications/warnings to user
            budgetRepository.save(budget);
        }
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        TransactionResponseDTO responseDTO = modelMapper.map(transaction, TransactionResponseDTO.class);
        responseDTO.setCurrency(transaction.getAccount().getCurrency());
        return responseDTO;
    }

    private BigDecimal validateEditRequest(TransactionEditRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findById((requestDTO.getTransactionId()))
                .orElseThrow(() -> {
                    throw new NotFoundException("Invalid transaction id.");
                });

        BigDecimal subtractFromAffectedBudgets = transaction.getAmount();

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {
                    throw new NotFoundException("Invalid account id.");
                });

        //TODO: SECURITY -> only for users with same id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new UnauthorizedException("You don't have permission to edit this budget.");
            //TODO: Security -> LOG OUT
        }
        if (transaction.getAccount().getAccountId() != requestDTO.getAccountId()) {
            throw new BadRequestException("Account id cannot be changed.");
        }
        if (!transactionTypeRepository.existsById(requestDTO.getTransactionTypeId())) {
            throw new NotFoundException("Invalid transaction type id.");
        }
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {
                    throw new NotFoundException("Invalid category id.");
                });
        if (category.getTransactionType().getTransactionTypeId() != (requestDTO.getTransactionTypeId())) {
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        if (!paymentMethodRepository.existsById(requestDTO.getPaymentMethodId())) {
            throw new NotFoundException("Invalid payment method id.");
        }
        return subtractFromAffectedBudgets;
    }


}
