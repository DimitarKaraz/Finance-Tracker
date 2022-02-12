package com.example.financetracker.service;

import com.example.financetracker.model.dto.AccountCreateRequestDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;


    public Account createAccount(AccountCreateRequestDTO requestDTO, int userId){
        Account account = modelMapper.map(requestDTO, Account.class);
        account.setUser(userRepository.getById(userId));
        accountRepository.save(account);
        return account;
    }


}
