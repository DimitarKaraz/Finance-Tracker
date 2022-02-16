package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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

    public TransactionResponseDTO getById(int transactionId){
        //todo security
        return convertToResponseDTO((transactionRepository.findById(transactionId)
                .orElseThrow(() -> {throw new NotFoundException("No transaction with this id.");})));
    }

    public List<TransactionResponseDTO> getAllByUserId(int userId){
        //todo security
        return transactionRepository.findAllByAccount_User_UserId(userId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getAllByAccountId(int accountId){
        //todo security
        return transactionRepository.findAllByAccount_AccountId(accountId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getAllByBudgetId(int id) {
        //todo securty

        return categoryRepository.findAllByBudgetId(id).stream()
                .map(category -> transactionRepository.findAllByCategoryCategoryId(category.getCategoryId()))
                .flatMap(Collection::stream)
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO requestDTO){
        //todo validations
        //todo security
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Transaction transaction = modelMapper.map(requestDTO, Transaction.class);

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});

        //TODO: SECURITY -> only for users with same id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new UnauthorizedException("You don't have permission to edit this budget.");
            //TODO: Security -> LOG OUT
        }

        transaction.setAccount(account);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).orElseThrow(() -> {throw new BadRequestException("Invalid transaction type.");} ));
        transaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        transaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId()).orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (transaction.getTransactionType().getTransactionTypeId() != transaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        transactionRepository.save(transaction);

        Set<Budget> affectedBudgets = budgetRepository.findAllBudgetsByCategoryAndAccount(requestDTO.getAccountId(), requestDTO.getCategoryId());
        updateAffectedBudgets(new BigDecimal(0), transaction.getAmount(), affectedBudgets);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO editTransaction(TransactionEditRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findById((requestDTO.getTransactionId()))
                .orElseThrow(() -> {
                    throw new NotFoundException("Invalid transaction id.");
                });
        BigDecimal subtractFromAffectedBudgets = transaction.getAmount();

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});

        //TODO: SECURITY -> only for users with same id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new UnauthorizedException("You don't have permission to edit this budget.");
            //TODO: Security -> LOG OUT
        }
        if (transaction.getAccount().getAccountId() != requestDTO.getAccountId()) {
            throw new BadRequestException("Account id cannot be changed.");
        }

        transaction.setAccount(account);
        transaction.setAmount(requestDTO.getAmount());
        transaction.setDateTime(requestDTO.getDateTime());
        transaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).orElseThrow(() -> {throw new BadRequestException("Invalid transaction type.");} ));
        transaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        transaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId()).orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (transaction.getTransactionType().getTransactionTypeId() != transaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        transactionRepository.save(transaction);

        Set<Budget> affectedBudgets = budgetRepository.findAllBudgetsByCategoryAndAccount(requestDTO.getAccountId(), requestDTO.getCategoryId());
        updateAffectedBudgets(subtractFromAffectedBudgets, transaction.getAmount(), affectedBudgets);

        return convertToResponseDTO(transaction);
    }

    public void deleteTransaction(int id) {
        if (!transactionRepository.existsById(id)) {
            throw new NotFoundException("Transaction does not exist.");
        }
        transactionRepository.deleteById(id);
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
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(transaction.getCategory(), CategoryResponseDTO.class);
        TransactionResponseDTO responseDTO = modelMapper.map(transaction, TransactionResponseDTO.class);
        responseDTO.setCategoryResponseDTO(categoryResponseDTO);
        responseDTO.setCurrency(transaction.getAccount().getCurrency());
        return responseDTO;
    }

}
