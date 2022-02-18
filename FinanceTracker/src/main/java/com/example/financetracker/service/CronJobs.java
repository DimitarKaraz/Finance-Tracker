package com.example.financetracker.service;

import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.RecurrentTransactionRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Component
public class CronJobs {

    @Autowired
    private RecurrentTransactionRepository recurrentTransactionRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetService budgetService;

    @Scheduled(cron = "0 0 0 * * *")
    public void RecurrentCronJob(){
        List<RecurrentTransaction> allRecurrentTransactions = recurrentTransactionRepository.findAllThatExpireOrNeedPaymentToday();
        for (RecurrentTransaction recurrentTransaction : allRecurrentTransactions){
            int totalDays = recurrentTransaction.getInterval().getDays()*recurrentTransaction.getIntervalCount();
            if (recurrentTransaction.getStartDate().plusDays(totalDays).equals(LocalDate.now())){
                Transaction transaction = new Transaction(recurrentTransaction);
                //todo make transactional method
                if (recurrentTransaction.getRemainingPayments() != null && recurrentTransaction.getRemainingPayments() > 0){
                    recurrentTransaction.setRemainingPayments(recurrentTransaction.getRemainingPayments()-1);
                }
                transactionRepository.save(transaction);
                if (recurrentTransaction.getEndDate() != null && recurrentTransaction.getEndDate().equals(LocalDate.now())){
                    recurrentTransactionRepository.delete(recurrentTransaction);
                }
                if (recurrentTransaction.getEndDate() == null && recurrentTransaction.getRemainingPayments() == 0){
                    recurrentTransactionRepository.delete(recurrentTransaction);
                }
                    recurrentTransaction.setStartDate(LocalDate.now());
                    recurrentTransactionRepository.save(recurrentTransaction);
            }
        }
    }


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
