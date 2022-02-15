package com.example.financetracker.service;

import com.example.financetracker.model.pojo.*;
import com.example.financetracker.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilityService {
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private CategoryIconRepository categoryIconRepository;
    @Autowired
    private IntervalRepository intervalRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public List<AccountType> getAllAccountTypes() {
        return accountTypeRepository.findAll();
    }

    public List<TransactionType> getAllTransactionTypes(){
        return transactionTypeRepository.findAll();
    }

    public List<CategoryIcon> getAllCategoryIcons(){
        return categoryIconRepository.findAll();
    }

    public List<Interval> getAllIntervals(){
        return intervalRepository.findAll();
    }
}
