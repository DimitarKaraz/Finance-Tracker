package com.example.financetracker.service;

import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class CronJobs {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetService budgetService;

    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    public void budgetCronJob() {
        List<Budget> budgets = budgetRepository.findAllCategoriesReadyForCronJob();
        resetAllBudgets(budgets);
    }

    @Transactional
    void resetAllBudgets(List<Budget> budgets) {
        budgets.forEach(budget -> {
            if (budget.getEndDate() != null) {              //then, it gets closed today
                budgetService.closeBudgetById(budget.getBudgetId());
            } else {                                        //then, it only gets reset today
                budget.setAmountSpent(new BigDecimal(0));
                budget.setStartDate(LocalDate.now());
                budgetRepository.save(budget);
            }
        });
    }

}
