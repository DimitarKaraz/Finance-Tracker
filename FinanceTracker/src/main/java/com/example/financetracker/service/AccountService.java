package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
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

    public AccountResponseDTO createAccount(AccountCreateRequestDTO requestDTO, int userId){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        if (accountRepository.findAccountByUser_UserIdAndName(userId, requestDTO.getName()) != null){
            throw new BadRequestException("Account with that name already exists.");
        }
        Account account = modelMapper.map(requestDTO, Account.class);
        account.setUser(userRepository.findByUserId(userId));
        accountRepository.save(account);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public List<AccountResponseDTO> getAllAccountsByUserId(int id) {
        List<Account> accounts = accountRepository.findAccountsByUser_UserId(id);
        return accounts.stream().map(account -> modelMapper.map(account, AccountResponseDTO.class))
                .collect(Collectors.toList());
    }

    public AccountResponseDTO getAccountById(int userId, int accountId) {
        Account acc = accountRepository.findAccountsByAccountId(accountId);
        if (acc.getUser().getUserId() != userId) {
            throw new UnauthorizedException("You do not have access to this user's accounts.");
        }
        return modelMapper.map(acc, AccountResponseDTO.class);
    }
}
