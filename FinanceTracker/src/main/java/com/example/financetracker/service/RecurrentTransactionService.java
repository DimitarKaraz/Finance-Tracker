package com.example.financetracker.service;


import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionEditRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RecurrentTransactionService {

    @Autowired
    private RecurrentTransactionRepository recurrentTransactionRepository;
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
    public RecurrentTransactionResponseDTO editRecurrentTransaction(RecurrentTransactionEditRequestDTO requestDTO){
        RecurrentTransaction recurrentTransaction = recurrentTransactionRepository.findById(requestDTO.getRecurrentTransactionTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid recurrent transaction id.");
        });
        recurrentTransaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid transaction type id.");}));
        recurrentTransaction.setName(requestDTO.getName());
        recurrentTransaction.setAmount(requestDTO.getAmount());
        //todo security for account_id
        if (accountRepository.existsById(requestDTO.getAccountId())){
            throw new NotFoundException("Invalid account id.");
        }
        recurrentTransaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");}));
        recurrentTransaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid payment method id.");}));
        recurrentTransaction.setEndDate(requestDTO.getEndDate());
        recurrentTransaction.setRemainingPayments(requestDTO.getRemainingPayments());
        recurrentTransactionRepository.save(recurrentTransaction);
        return convertToResponseDTO(recurrentTransaction);
    }

    private RecurrentTransactionResponseDTO convertToResponseDTO(RecurrentTransaction recurrentTransaction) {
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(recurrentTransaction.getCategory(), CategoryResponseDTO.class);
        RecurrentTransactionResponseDTO responseDTO = modelMapper.map(recurrentTransaction, RecurrentTransactionResponseDTO.class);
        responseDTO.setCategoryResponseDTO(categoryResponseDTO);
        responseDTO.setCurrency(recurrentTransaction.getAccount().getCurrency());
        return responseDTO;
    }

    public void deleteRecurrentTransaction(int id) {
        if (!recurrentTransactionRepository.existsById(id)) {
            throw new NotFoundException("Recurrent transaction does not exist.");
        }
        recurrentTransactionRepository.deleteById(id);
    }

}
