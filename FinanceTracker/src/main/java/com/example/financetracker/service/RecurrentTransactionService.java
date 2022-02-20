package com.example.financetracker.service;


import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionCreateRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionEditRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
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
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public RecurrentTransactionResponseDTO createRecurrentTransaction(RecurrentTransactionCreateRequestDTO requestDTO) {
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RecurrentTransaction recurrentTransaction = modelMapper.map(requestDTO, RecurrentTransaction.class);
        if (requestDTO.getEndDate() != null && requestDTO.getRemainingPayments() != null) {
            throw new BadRequestException("You must select end date, remaining payment count or forever.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
        }
        recurrentTransaction.setAccount(account);
        recurrentTransaction.setInterval(intervalRepository.findById(requestDTO.getIntervalId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid interval id.");}));
        recurrentTransaction.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type id.");} ));
        recurrentTransaction.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category id,");}));
        recurrentTransaction.setPaymentMethod(paymentMethodRepository.findById(requestDTO.getPaymentMethodId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid payment method id.");}));
        if (recurrentTransaction.getTransactionType().getTransactionTypeId() !=
                recurrentTransaction.getCategory().getTransactionType().getTransactionTypeId()){
            throw new BadRequestException("Category - transaction type mismatch.");
        }
        if (recurrentTransaction.getCategory().getUser() == null) {
            throw new BadRequestException("You cannot edit this category.");
        }
        if (recurrentTransaction.getCategory().getUser().getUserId() != account.getUser().getUserId()) {
            throw new ForbiddenException("You do not have access to this category.");
        }
        if (recurrentTransaction.getStartDate().equals(LocalDate.now())){
            Transaction transaction = new Transaction(recurrentTransaction);
            transactionRepository.save(transaction);
        }

        recurrentTransactionRepository.save(recurrentTransaction);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return convertToResponseDTO(recurrentTransaction);
    }

    public List<RecurrentTransactionResponseDTO> getAllRecurrentTransactionsForCurrentUser() {
        int userId = MyUserDetailsService.getCurrentUserId();
        return recurrentTransactionRepository.findAllByAccount_User_UserId(userId).stream()
                .map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public RecurrentTransactionResponseDTO getRecurrentTransactionById(int recurrentTransactionId) {
        RecurrentTransaction recurrentTransaction = recurrentTransactionRepository.findById(recurrentTransactionId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid recurrent transaction id.");});
        if (recurrentTransaction.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this recurrent transaciton.");
        }
        return convertToResponseDTO(recurrentTransaction);
    }

    @Transactional
    public RecurrentTransactionResponseDTO editRecurrentTransaction(RecurrentTransactionEditRequestDTO requestDTO){
        RecurrentTransaction recurrentTransaction = recurrentTransactionRepository.findById(requestDTO.getRecurrentTransactionId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid recurrent transaction id.");});
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        if (requestDTO.getEndDate() != null && requestDTO.getRemainingPayments() != null) {
            throw new BadRequestException("You must select end date, remaining payment count or forever.");
        }
        if (requestDTO.getEndDate() != null && recurrentTransaction.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be past end date.");
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
                recurrentTransaction.getCategory().getUser().getUserId() != account.getUser().getUserId()) {
            throw new BadRequestException("You do not have access to this category.");
        }

        recurrentTransaction.setName(requestDTO.getName());
        recurrentTransaction.setAmount(requestDTO.getAmount());
        recurrentTransaction.setEndDate(requestDTO.getEndDate());
        recurrentTransaction.setRemainingPayments(requestDTO.getRemainingPayments());
        recurrentTransactionRepository.save(recurrentTransaction);
        return convertToResponseDTO(recurrentTransaction);
    }

    public void deleteRecurrentTransaction(int recurrentTransactionId) {
        RecurrentTransaction recurrentTransaction = recurrentTransactionRepository.findById(recurrentTransactionId)
                .orElseThrow(() -> {throw new NotFoundException("Recurrent transaction does not exist.");});
        if (recurrentTransaction.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        recurrentTransactionRepository.deleteById(recurrentTransactionId);
    }

    private RecurrentTransactionResponseDTO convertToResponseDTO(RecurrentTransaction recurrentTransaction) {
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(recurrentTransaction.getCategory(), CategoryResponseDTO.class);
        RecurrentTransactionResponseDTO responseDTO = modelMapper.map(recurrentTransaction, RecurrentTransactionResponseDTO.class);
        responseDTO.setCategoryResponseDTO(categoryResponseDTO);
        responseDTO.setCurrency(recurrentTransaction.getAccount().getCurrency());
        return responseDTO;
    }

}
