package com.example.financetracker.service;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.TransactionType;
import com.example.financetracker.model.repositories.AccountTypeRepository;
import com.example.financetracker.model.repositories.CategoryIconRepository;
import com.example.financetracker.model.repositories.CurrencyRepository;
import com.example.financetracker.model.repositories.TransactionTypeRepository;
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

}
