package com.example.financetracker.service;

import com.example.financetracker.model.repositories.ClosedBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClosedBudgetService {

    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
}
