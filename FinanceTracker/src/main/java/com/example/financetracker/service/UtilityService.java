package com.example.financetracker.service;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.repositories.AccountTypeRepository;
import com.example.financetracker.model.repositories.CurrencyRepository;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilityService {
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    public List<AccountType> getAllAccountTypes() {
        return accountTypeRepository.findAll();
    }
}
