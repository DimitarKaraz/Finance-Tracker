package com.example.financetracker.controller;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.TransactionType;
import com.example.financetracker.service.UtilityService;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtilityController {

    @Autowired
    private UtilityService utilityService;

    //todo Return ResponseEntity everywhere!

    @GetMapping("/currencies")
    public List<Currency> getAllCurrencies(){
        return utilityService.getAllCurrencies();
    }

    @GetMapping("/account_types")
    public List<AccountType> getAllAccountTypes(){
        return utilityService.getAllAccountTypes();
    }

    @GetMapping("/transaction_types")
    public List<TransactionType> getAllTransactionTypes(){
        return utilityService.getAllTransactionTypes();
    }

    @GetMapping("/category_icons")
    public List<CategoryIcon> getAllCategoryIcons(){
        return utilityService.getAllCategoryIcons();
    }
}
