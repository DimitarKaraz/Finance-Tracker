package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
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

import javax.transaction.Transactional;
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

    @Transactional
    public AccountResponseDTO createAccount(AccountCreateRequestDTO requestDTO, int userId){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        if (accountRepository.findAccountByUser_UserIdAndName(userId, requestDTO.getName()) != null){
            throw new BadRequestException("An account with that name already exists.");
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

    @Transactional
    public AccountResponseDTO editAccount(AccountCreateRequestDTO requestDTO, int userId, int accountId){
        if (accountRepository.findAccountByUser_UserIdAndName(userId, requestDTO.getName()) != null){
            throw new BadRequestException("An account with that name already exists.");
        }
        Account account = modelMapper.map(requestDTO, Account.class);
        account.setAccountId(accountId);
        account.setAccountType(requestDTO.getAccountType());
        account.setBalance(requestDTO.getBalance());
        account.setCurrency(requestDTO.getCurrency());  //TODO: recalculate absolute value
        account.setName(requestDTO.getName());
        account.setUser(userRepository.findByUserId(userId));
        accountRepository.save(account);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public void deleteAccount(int accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Account does not exist.");
        }
//        if (accountRepository.getById(accountId).isDefault()) {
//            throw new BadRequestException("This account cannot be deleted.");
//        }
        accountRepository.deleteById(accountId);
        if (accountRepository.existsById(accountId)) {
            throw new NotFoundException("Failed to delete account.");
        }
    }
}
