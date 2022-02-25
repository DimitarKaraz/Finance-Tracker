package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class TransactionService {
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
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO requestDTO) {
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Transaction transaction = modelMapper.map(requestDTO, Transaction.class);
        transaction.setAccount(account);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type.");} ));
        transaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        transaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (transaction.getTransactionType().getTransactionTypeId() != transaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        transactionRepository.save(transaction);

        if (transaction.getTransactionType().getName().equalsIgnoreCase("expense")) {
            updateAffectedBudgets(new BigDecimal(0), transaction.getAmount(),
                    requestDTO.getAccountId(), requestDTO.getCategoryId(), transaction.getDateTime().toLocalDate());
        }
        updateAccountBalance(null, transaction);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToResponseDTO(transaction);
    }

    public List<TransactionResponseDTO> getAllTransactionsByCurrentUser(){
        int userId = MyUserDetailsService.getCurrentUserId();
        return transactionRepository.findAllByAccount_User_UserId(userId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionsById(int transactionId){
        Transaction transaction = (transactionRepository.findById(transactionId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid transaction id.");}));
        if (transaction.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this transaction.");
        }
        return convertToResponseDTO(transaction);
    }

    public List<TransactionResponseDTO> getAllTransactionsByAccountId(int accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        return transactionRepository.findAllByAccount_AccountId(accountId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getAllTransactionsByBudgetId(int budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid budget id.");});
        if (budget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this budget.");
        }
        Set<Category> categories = categoryRepository.findAllByBudgetId(budgetId);

        List<Transaction> transactionsByCategoryIds = transactionRepository
                .findTransactionsByCategoryIsInAndAccountUserUserId(categories, budget.getAccount().getUser().getUserId());
        List<Transaction> transactionsByStartDate = transactionRepository
                .findTransactionsByDateTimeAfter(LocalDateTime.of(budget.getStartDate(), LocalTime.now()));
        List<Transaction> transactionsByUserId = transactionRepository
                .findAllByAccount_User_UserId(budget.getAccount().getUser().getUserId());

        transactionsByCategoryIds.retainAll(transactionsByStartDate);
        transactionsByCategoryIds.retainAll(transactionsByUserId);

        return transactionsByCategoryIds.stream()
                .map(transaction -> convertToResponseDTO(transaction))
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDTO editTransaction(TransactionEditRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findById((requestDTO.getTransactionId()))
                .orElseThrow(() -> {throw new NotFoundException("Invalid transaction id.");});

        Transaction oldTransaction = new Transaction(transaction);

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }

        if (transaction.getAccount().getAccountId() != requestDTO.getAccountId()) {
            throw new BadRequestException("Account id cannot be changed.");
        }

        transaction.setAmount(requestDTO.getAmount());
        transaction.setDateTime(requestDTO.getDateTime());
        transaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type.");} ));
        transaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        transaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (transaction.getTransactionType().getTransactionTypeId() !=
                transaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        transactionRepository.save(transaction);

        if (transaction.getTransactionType().getName().equalsIgnoreCase("expense")) {
            if (!oldTransaction.getCategory().equals(transaction.getCategory())) {
                //Old budgets:
                updateAffectedBudgets(oldTransaction.getAmount(), new BigDecimal(0),
                        requestDTO.getAccountId(), oldTransaction.getCategory().getCategoryId(), oldTransaction.getDateTime().toLocalDate());
                //New budgets:
                updateAffectedBudgets(new BigDecimal(0), transaction.getAmount(),
                        requestDTO.getAccountId(), transaction.getCategory().getCategoryId(), transaction.getDateTime().toLocalDate());
            } else {
                updateAffectedBudgets(oldTransaction.getAmount(), transaction.getAmount(),
                        requestDTO.getAccountId(), transaction.getCategory().getCategoryId(), oldTransaction.getDateTime().toLocalDate());
            }
        }
        updateAccountBalance(oldTransaction, transaction);

        return convertToResponseDTO(transaction);
    }

    @Transactional
    public void deleteTransaction(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {throw new NotFoundException("Transaction does not exist.");});
        if (transaction.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this transaction.");
        }
        if (transaction.getTransactionType().getName().equalsIgnoreCase("expense")) {
            updateAffectedBudgets(transaction.getAmount(), new BigDecimal(0),
                    transaction.getAccount().getAccountId(), transaction.getCategory().getCategoryId(), transaction.getDateTime().toLocalDate());
        }
        updateAccountBalance(transaction, null);
        transactionRepository.deleteById(transactionId);
    }

    public void updateAffectedBudgets(BigDecimal oldTransactionAmount, BigDecimal newTransactionAmount, int accountId, int categoryId, LocalDate date) {
        Set<Budget> affectedBudgets = budgetRepository.findAllByCategoryIdAndAccountIdAndStartDate(accountId, categoryId, date);
        for (Budget budget : affectedBudgets){
            budget.setAmountSpent(budget.getAmountSpent().subtract(oldTransactionAmount));
            budget.setAmountSpent(budget.getAmountSpent().add(newTransactionAmount));
            budgetRepository.save(budget);
        }
    }

    private void updateAccountBalance(Transaction oldTransaction, Transaction newTransaction) {
        if (newTransaction != null) {
            BigDecimal newAmount = newTransaction.getAmount();
            if (newTransaction.getTransactionType().getName().equalsIgnoreCase("expense")) {
                newAmount = newAmount.negate();
            }
            newTransaction.getAccount().setBalance(newTransaction.getAccount().getBalance().add(newAmount));
            accountRepository.save(newTransaction.getAccount());
        }
        if (oldTransaction != null) {
            BigDecimal oldAmount = oldTransaction.getAmount();
            if (oldTransaction.getTransactionType().getName().equalsIgnoreCase("expense")) {
                oldAmount = oldAmount.negate();
            }
            oldTransaction.getAccount().setBalance(oldTransaction.getAccount().getBalance().subtract(oldAmount));
            accountRepository.save(oldTransaction.getAccount());
        }
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(transaction.getCategory(), CategoryResponseDTO.class);
        TransactionResponseDTO responseDTO = modelMapper.map(transaction, TransactionResponseDTO.class);
        responseDTO.setCategoryResponseDTO(categoryResponseDTO);
        responseDTO.setCurrency(transaction.getAccount().getCurrency());
        return responseDTO;
    }

    private void applyFilters(List<Transaction> transactionsByDatesAndFilters, TransactionByFiltersRequestDTO requestDTO){
        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty() && requestDTO.getTransactionTypeId() != null){
            List<Category> chosenCategories = categoryRepository.findAllByCategoryIdIsIn(requestDTO.getCategoryIds());
            for (Category category : chosenCategories){
                if (category.getTransactionType().getTransactionTypeId() != requestDTO.getTransactionTypeId()){
                    throw new BadRequestException("Category transaction types have to match selected transaction type.");
                }
            }
        }
        if (requestDTO.getAmountMin() != null && requestDTO.getAmountMax() != null){
            List<Transaction> transactionsByAmount = transactionRepository.findAllByAmountBetween(requestDTO.getAmountMin(), requestDTO.getAmountMax());
            transactionsByDatesAndFilters.retainAll(transactionsByAmount);
        }
        if (requestDTO.getAccountId() != null){
            List<Transaction> transactionsByAccountId = transactionRepository.findAllByAccount_AccountId(requestDTO.getAccountId());
            transactionsByDatesAndFilters.retainAll(transactionsByAccountId);
        }
        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty()){
            List<Transaction> transactionsByCategoryIds = transactionRepository.findAllByCategory_CategoryIdIsIn(requestDTO.getCategoryIds());
            transactionsByDatesAndFilters.retainAll(transactionsByCategoryIds);
        }
        if (requestDTO.getTransactionTypeId() != null){
            List<Transaction> transactionsByTransactionTypeId = transactionRepository.findAllByTransactionType_TransactionTypeId(requestDTO.getTransactionTypeId());
            transactionsByDatesAndFilters.retainAll(transactionsByTransactionTypeId);
        }
        if (requestDTO.getPaymentMethodId() != null){
            List<Transaction> transactionsByPaymentMethodId = transactionRepository.findAllByPaymentMethod_PaymentMethodId(requestDTO.getPaymentMethodId());
            transactionsByDatesAndFilters.retainAll(transactionsByPaymentMethodId);
        }
    }
}
