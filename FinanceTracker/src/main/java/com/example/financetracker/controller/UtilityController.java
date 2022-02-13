package com.example.financetracker.controller;

import com.example.financetracker.model.pojo.AccountType;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtilityController extends AbstractController {

    @Autowired
    private UtilityService utilityService;

    @GetMapping("/currencies")
    public List<Currency> getAllCurrencies(){
        return utilityService.getAllCurrencies();
    }

    @GetMapping("/account_types")
    public List<AccountType> getAllAccountTypes(){
        return utilityService.getAllAccountTypes();
    }

}