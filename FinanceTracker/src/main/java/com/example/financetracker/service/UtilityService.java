package com.example.financetracker.service;

import com.example.financetracker.model.pojo.*;
import com.example.financetracker.model.repositories.*;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
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

