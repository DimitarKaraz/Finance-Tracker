package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
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
        if (accountRepository.findAccountByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null){
            throw new BadRequestException("An account with that name already exists.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Account account = modelMapper.map(requestDTO, Account.class);
        account.setUser(userRepository.findByUserId(requestDTO.getUserId()));
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId()).orElse(null));
        account.setCurrency(currencyRepository.findById(requestDTO.getCurrencyId()).orElse(null));
        accountRepository.save(account);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public List<AccountResponseDTO> getAllAccountsByUserId(int id) {
        userRepository.findById(id).orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
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


    public AccountResponseDTO editAccount(AccountEditRequestDTO requestDTO){
        //TODO: SECURITY -> only for users with same id

        if (!accountRepository.existsById(requestDTO.getAccountId())) {
            throw new BadRequestException("Invalid account id.");
        }
        if (accountRepository.findById(requestDTO.getAccountId()).get().getUser().getUserId() != requestDTO.getUserId()) {
            throw new BadRequestException("Invalid user id.");
        }
        Account account = modelMapper.map(requestDTO, Account.class);
        if (!account.getName().equals(requestDTO.getName())) {
            if (accountRepository.findAccountByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null) {
                throw new BadRequestException("An account with that name already exists.");
            }
        }
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId()).orElse(null));
        account.setBalance(requestDTO.getBalance());
        account.setCurrency(currencyRepository.findById(requestDTO.getCurrencyId()).orElse(null));  //TODO: recalculate absolute value
        account.setName(requestDTO.getName());
        account.setUser(userRepository.findByUserId(requestDTO.getUserId()));
        accountRepository.save(account);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public void deleteAccount(int id) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("Account does not exist.");
        }
//        if (accountRepository.getById(accountId).isDefault()) {
//            throw new BadRequestException("This account cannot be deleted.");
//        }
        accountRepository.deleteById(id);
    }
}
