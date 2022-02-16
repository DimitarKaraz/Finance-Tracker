package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
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
import java.util.List;

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
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TransactionResponseDTO editTransaction(TransactionEditRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findById((requestDTO.getTransactionId()))
                .orElseThrow(() -> {throw new NotFoundException("Invalid transaction id.");});

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
        if (!transactionTypeRepository.existsById(requestDTO.getTransactionTypeId())) {
            throw new NotFoundException("Invalid transaction type id.");
        }
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
        if (category.getTransactionType().getTransactionTypeId() != (requestDTO.getTransactionTypeId())) {
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        if (!paymentMethodRepository.existsById(requestDTO.getPaymentMethodId())) {
            throw new NotFoundException("Invalid payment method id.");
        }
        transaction = modelMapper.map(requestDTO, Transaction.class);
        transactionRepository.save(transaction);

        List<Budget> budgets = transaction.
    }


}
