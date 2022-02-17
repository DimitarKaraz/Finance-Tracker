package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountEditRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.AccountTypeRepository;
import com.example.financetracker.model.repositories.CurrencyRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public AccountResponseDTO createAccount(AccountCreateRequestDTO requestDTO){
        if (accountRepository.existsAccountByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName())){
            throw new BadRequestException("An account with that name already exists.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Account account = modelMapper.map(requestDTO, Account.class);

        account.setUser(userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");}));
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account type id.");}));
        account.setCurrency(currencyRepository.findById(requestDTO.getCurrencyId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid currency id.");}));

        accountRepository.save(account);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public List<AccountResponseDTO> getAllAccountsByUserId(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        List<Account> accounts = accountRepository.findAccountsByUser_UserId(userId);
        return accounts.stream().map(account -> modelMapper.map(account, AccountResponseDTO.class))
                .collect(Collectors.toList());
    }

    public AccountResponseDTO getAccountById(int userId, int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != userId) {
            throw new UnauthorizedException("You do not have access to this user's accounts.");
        }
        return modelMapper.map(account, AccountResponseDTO.class);
    }


    public AccountResponseDTO editAccount(AccountEditRequestDTO requestDTO){
        //TODO: SECURITY -> only for users with same id
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != requestDTO.getUserId()) {
            throw new ForbiddenException("You do not have permission to edit this account.");
        }
        if (!account.getName().equals(requestDTO.getName())) {
            if (accountRepository.existsAccountByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName())) {
                throw new BadRequestException("An account with that name already exists.");
            }
        }
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account type id.");}));
        account.setCurrency(currencyRepository.findById(requestDTO.getCurrencyId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid currency id.");}));
        //TODO: recalculate absolute value
        account.setBalance(requestDTO.getBalance());
        account.setName(requestDTO.getName());
        accountRepository.save(account);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public void deleteAccountById(int id) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("Account does not exist.");
        }
        accountRepository.deleteById(id);
    }
}
