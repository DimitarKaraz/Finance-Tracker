package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.pojo.*;
import com.example.financetracker.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtilityController {

    @Autowired
    private UtilityService utilityService;

    @GetMapping("/currencies")
    public ResponseEntity<ResponseWrapper<List<Currency>>> getAllCurrencies(){
        return ResponseWrapper.wrap("All currencies retrieved.", utilityService.getAllCurrencies(), HttpStatus.OK);
    }

    @GetMapping("/account_types")
    public ResponseEntity<ResponseWrapper<List<AccountType>>> getAllAccountTypes(){
        return ResponseWrapper.wrap("All account types retrieved.", utilityService.getAllAccountTypes(), HttpStatus.OK);
    }

    @GetMapping("/transaction_types")
    public ResponseEntity<ResponseWrapper<List<TransactionType>>> getAllTransactionTypes(){
        return ResponseWrapper.wrap("All currencies retrieved.", utilityService.getAllTransactionTypes(), HttpStatus.OK);
    }

    @GetMapping("/category_icons")
    public ResponseEntity<ResponseWrapper<List<CategoryIcon>>> getAllCategoryIcons(){
        return ResponseWrapper.wrap("All currencies retrieved.", utilityService.getAllCategoryIcons(), HttpStatus.OK);
    }

    @GetMapping("intervals")
    public ResponseEntity<ResponseWrapper<List<Interval>>> getAllIntervals(){
        return ResponseWrapper.wrap("All intervals retrieved.", utilityService.getAllIntervals(), HttpStatus.OK);
    }
}
