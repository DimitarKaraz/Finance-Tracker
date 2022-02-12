package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.accountDTOs.AccountDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
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

//accountRepository.existsByName(requestDTO.getName())
    public AccountDTO createAccount(AccountCreateRequestDTO requestDTO, int userId){
        if (accountRepository.findByName(requestDTO.getName()) != null){
            throw new BadRequestException("Account with that name already exists.");
        }
        Account account = modelMapper.map(requestDTO, Account.class);
        account.setUser(userRepository.findByUserId(userId));
        accountRepository.save(account);
        return modelMapper.map(account, AccountDTO.class);
    }

    public List<AccountDTO> getAllAccountsByUserId(int id) {
        List<Account> accounts = accountRepository.findAccountsByUser_UserId(id);
        return accounts.stream().map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(int userId, int accountId) {
        Account acc = accountRepository.findAccountsByAccountId(accountId);
        if (acc.getUser().getUserId() != userId) {
            throw new UnauthorizedException("You do not have access to this user's accounts.");
        }
        return modelMapper.map(acc, AccountDTO.class);
    }
}
