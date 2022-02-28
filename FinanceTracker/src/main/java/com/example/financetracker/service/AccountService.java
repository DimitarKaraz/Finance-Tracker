package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountEditRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.AccountTypeRepository;
import com.example.financetracker.model.repositories.CurrencyRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class AccountService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${default.page.size}")
    private int pageSize;

    public AccountResponseDTO createAccount(AccountCreateRequestDTO requestDTO){
        int userId = MyUserDetailsService.getCurrentUserId();
        if (accountRepository.existsAccountByUser_UserIdAndName(userId, requestDTO.getName())){
            throw new BadRequestException("An account with that name already exists.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Account account = modelMapper.map(requestDTO, Account.class);

        account.setUser(userRepository.findById(userId)
                .orElseThrow(() -> {throw new UnauthorizedException("Invalid user id.");}));
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account type id.");}));
        account.setCurrency(currencyRepository.findById(requestDTO.getCurrencyId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid currency id.");}));

        accountRepository.save(account);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public Map<String, Object> getAllAccountsOfCurrentUser(int pageNo) {
        int userId = MyUserDetailsService.getCurrentUserId();
        Page<Account> accountsPage = accountRepository.findAccountsByUser_UserId(userId
                    , PageRequest.of(pageNo, pageSize, Sort.by("name")));

        return convertToMap(accountsPage);
    }

    public AccountResponseDTO getAccountById(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public AccountResponseDTO editAccount(AccountEditRequestDTO requestDTO){
        int userId = MyUserDetailsService.getCurrentUserId();
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != userId) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        if (!account.getName().equals(requestDTO.getName())) {
            if (accountRepository.existsAccountByUser_UserIdAndName(userId, requestDTO.getName())) {
                throw new BadRequestException("An account with that name already exists.");
            }
        }
        account.setAccountType(accountTypeRepository.findById(requestDTO.getAccountTypeId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid account type id.");}));
        Currency newCurrency = currencyRepository.findById(requestDTO.getCurrencyId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid currency id.");});
        if (requestDTO.getCurrencyId() != account.getCurrency().getCurrencyId()) {
            account.setBalance(currencyExchangeService.calculateCurrencyConversion(account.getCurrency().getAbbreviation(),
                    newCurrency.getAbbreviation(), account.getBalance()));
            System.out.println(account.getBalance().doubleValue());
        } else {
            account.setBalance(requestDTO.getBalance());
        }
        account.setCurrency(newCurrency);
        account.setName(requestDTO.getName());
        accountRepository.save(account);
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    public void deleteAccountById(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid account id.");});
        if (account.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this account.");
        }
        accountRepository.deleteById(accountId);
    }

    private Map<String, Object> convertToMap(Page<Account> closedBudgetsPage) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalItems", closedBudgetsPage.getTotalElements());
        map.put("currentPage", closedBudgetsPage.getNumber());
        map.put("totalPages", closedBudgetsPage.getTotalPages());
        map.put("Accounts", closedBudgetsPage.getContent().stream()
                .map(account -> modelMapper.map(account, AccountResponseDTO.class))
                .collect(Collectors.toList()));
        return map;
    }
}
