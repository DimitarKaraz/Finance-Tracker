package com.example.financetracker.service;

import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.accountDTOs.AccountDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
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
