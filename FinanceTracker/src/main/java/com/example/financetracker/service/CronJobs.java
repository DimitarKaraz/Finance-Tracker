package com.example.financetracker.service;

import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.RecurrentTransactionRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    @Retryable( value = Exception.class,
            maxAttempts = 10, backoff = @Backoff(delay = 60*1000))
    public void recurrentCronJob(){
        List<RecurrentTransaction> allRecurrentTransactions = recurrentTransactionRepository.findAllThatExpireOrNeedPaymentToday();
        executeAllRecurrentTransactions(allRecurrentTransactions);
    }

    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    @Retryable( value = Exception.class,
            maxAttempts = 10, backoff = @Backoff(delay = 60*1000))
    public void budgetCronJob() {
        List<Budget> budgets = budgetRepository.findAllBudgetsReadyForCronJob();
        resetAllBudgets(budgets);
    }

    @Transactional
    public void executeAllRecurrentTransactions(List<RecurrentTransaction> allRecurrentTransactions){
        for (RecurrentTransaction recurrentTransaction : allRecurrentTransactions){
            int totalDays = recurrentTransaction.getInterval().getDays()*recurrentTransaction.getIntervalCount();
            if (recurrentTransaction.getStartDate().plusDays(totalDays).equals(LocalDate.now())){
                if (recurrentTransaction.getRemainingPayments() != null && recurrentTransaction.getRemainingPayments() > 0){
                    recurrentTransaction.setRemainingPayments(recurrentTransaction.getRemainingPayments()-1);
                }
                Transaction transaction = new Transaction(recurrentTransaction);
                transactionRepository.save(transaction);
                recurrentTransaction.setStartDate(LocalDate.now());
                recurrentTransactionRepository.save(recurrentTransaction);
            }
            if (recurrentTransaction.getEndDate() != null && recurrentTransaction.getEndDate().equals(LocalDate.now())){
                recurrentTransactionRepository.delete(recurrentTransaction);
                continue;
            }
            if (recurrentTransaction.getEndDate() == null && recurrentTransaction.getRemainingPayments() == 0){
                recurrentTransactionRepository.delete(recurrentTransaction);
            }
        }
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

    //todo implement recovery method
    @Recover
    @SneakyThrows
    void logger(Exception e){
        File folder = new File("logs");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String fileName = folder.getName() + File.separator + "cronJob_fail_log_"+ LocalDateTime.now() +".txt";
        String text = "Message: " + e.getMessage() +
                "\nStack trace: " + Arrays.toString(e.getStackTrace());
        Files.write(Path.of(fileName), text.getBytes(), StandardOpenOption.CREATE);
    }


    //todo make cronjob for emails to inactive users
}
