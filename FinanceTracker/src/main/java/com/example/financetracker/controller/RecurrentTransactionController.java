package com.example.financetracker.controller;

import com.example.financetracker.service.RecurrentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecurrentTransactionController {

    @Autowired
    private RecurrentTransactionService recurrentTransactionService;


}
