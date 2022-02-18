package com.example.financetracker.service;


import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionCreateRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionEditRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private IntervalRepository intervalRepository;
    @Autowired
    private ModelMapper modelMapper;


    public RecurrentTransactionResponseDTO getRecurrentTransactionById(int recurrentTransactionId) {
        //todo security
        return convertToResponseDTO((recurrentTransactionRepository.findById(recurrentTransactionId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid recurrent transaction id.");})));
    }

    public List<RecurrentTransactionResponseDTO> getAllRecurrentTransactionsByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Invalid user id.");
        }
        return recurrentTransactionRepository.findAllByAccount_User_UserId(userId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public RecurrentTransactionResponseDTO createRecurrentTransaction(RecurrentTransactionCreateRequestDTO requestDTO) {
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});

        //TODO: security -> check if account.findById(requestDTO.getAccountId).getUser.getUserId == session user_id
        if (!userRepository.existsById(account.getUser().getUserId())) {
            throw new UnauthorizedException("You don't have permission to edit this budget.");
            //TODO: Security -> LOG OUT
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RecurrentTransaction recurrentTransaction = modelMapper.map(requestDTO, RecurrentTransaction.class);

        recurrentTransaction.setAccount(account);
        recurrentTransaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type.");} ));
        recurrentTransaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        recurrentTransaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (recurrentTransaction.getTransactionType().getTransactionTypeId() !=
                recurrentTransaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        if (recurrentTransaction.getCategory().getUser() != null &&
                recurrentTransaction.getCategory().getUser().getUserId() != account.getUser().getUserId()) {
            //TODO: LOGOUT hacker
            throw new ForbiddenException("You cannot access this category.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getRemainingPayments() != null) {
            throw new BadRequestException("You must select either end date or remaining payment count.");
        }
        if (requestDTO.getEndDate() == null && requestDTO.getRemainingPayments() == null) {
            throw new BadRequestException("You must select either end date or remaining payment count.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        recurrentTransaction.setStartDate(requestDTO.getStartDate());
        recurrentTransaction.setInterval(intervalRepository.findById(requestDTO.getIntervalId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid interval id.");}));
//        recurrentTransaction.setIntervalCount(requestDTO.getIntervalCount());
//        recurrentTransaction.setEndDate(requestDTO.getEndDate());
//        if (requestDTO.getRemainingPayments() == null) {
//        recurrentTransaction.setRemainingPayments(null);
//        }
        recurrentTransactionRepository.save(recurrentTransaction);

        //TODO: cron job method

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToResponseDTO(recurrentTransaction);
    }

    @Transactional
    public RecurrentTransactionResponseDTO editRecurrentTransaction(RecurrentTransactionEditRequestDTO requestDTO){
        RecurrentTransaction recurrentTransaction = recurrentTransactionRepository.findById(requestDTO.getRecurrentTransactionId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid recurrent transaction id.");});
        //todo security for account_id
        if (!accountRepository.existsById(requestDTO.getAccountId())){
            throw new NotFoundException("Invalid account id.");
        }
        recurrentTransaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid transaction type id.");}));
        recurrentTransaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");}));
        recurrentTransaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid payment method id.");}));
        if (recurrentTransaction.getTransactionType().getTransactionTypeId() !=
                recurrentTransaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        if (recurrentTransaction.getCategory().getUser() != null &&
                recurrentTransaction.getCategory().getUser().getUserId() != recurrentTransaction.getAccount().getUser().getUserId()) {
            throw new BadRequestException("You cannot access this category.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getRemainingPayments() != null) {
            throw new BadRequestException("You must select either end date or remaining payment count.");
        }
        if (requestDTO.getEndDate() == null && requestDTO.getRemainingPayments() == null) {
            throw new BadRequestException("You must select either end date or remaining payment count.");
        }
        if (requestDTO.getEndDate() != null && recurrentTransaction.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        recurrentTransaction.setName(requestDTO.getName());
        recurrentTransaction.setAmount(requestDTO.getAmount());
        recurrentTransaction.setEndDate(requestDTO.getEndDate());
        recurrentTransaction.setRemainingPayments(requestDTO.getRemainingPayments());
        recurrentTransactionRepository.save(recurrentTransaction);
        return convertToResponseDTO(recurrentTransaction);
    }

    public void deleteRecurrentTransaction(int id) {
        if (!recurrentTransactionRepository.existsById(id)) {
            throw new NotFoundException("Recurrent transaction does not exist.");
        }
        recurrentTransactionRepository.deleteById(id);
    }

    private RecurrentTransactionResponseDTO convertToResponseDTO(RecurrentTransaction recurrentTransaction) {
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(recurrentTransaction.getCategory(), CategoryResponseDTO.class);
        RecurrentTransactionResponseDTO responseDTO = modelMapper.map(recurrentTransaction, RecurrentTransactionResponseDTO.class);
        responseDTO.setCategoryResponseDTO(categoryResponseDTO);
        responseDTO.setCurrency(recurrentTransaction.getAccount().getCurrency());
        return responseDTO;
    }

}
