package com.example.financetracker.service;

import com.example.financetracker.model.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;


}
