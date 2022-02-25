package com.example.financetracker.service;

import com.example.financetracker.model.pojo.*;
import com.example.financetracker.model.repositories.*;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 0 * * *")    // every day at midnight
    @Retryable( value = Exception.class,
            maxAttempts = 5, backoff = @Backoff(delay = 60*1000))
    public void recurrentCronJob(){
        List<RecurrentTransaction> allRecurrentTransactions = recurrentTransactionRepository.findAllThatExpireOrNeedPaymentToday();
        executeAllRecurrentTransactions(allRecurrentTransactions);
    }

    @Scheduled(cron = "0 0 0 * * *")   // every day at midnight
    @Retryable(value = Exception.class,
            maxAttempts = 5, backoff = @Backoff(delay = 60*1000))
    public void budgetCronJob()  {
        List<Budget> budgets = budgetRepository.findAllBudgetsReadyForCronJob();
        resetAllBudgets(budgets);
    }

    @Scheduled(cron = "0 0 0 * * *")  // every day at midnight
    @Retryable(value = Exception.class,
            maxAttempts = 5, backoff = @Backoff(delay = 60*1000))
    public void sendEmailToInactiveUsersCronJob() {
        List<User> inactiveUsers = userRepository.findAllInactiveUsers();
        sendAllTheEmails(inactiveUsers);
    }

    @Recover
    @SneakyThrows
    public void logger(Exception e){
        File folder = new File("logs");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String fileName = folder.getName() + File.separator + "cron_job_fail_"+ System.nanoTime() +".txt";
        String text = "Message: " + e.getMessage() + "\nTimestamp:" + LocalDateTime.now() +
                "\nStack trace: " + Arrays.toString(e.getStackTrace());
        Files.write(Path.of(fileName), text.getBytes(), StandardOpenOption.CREATE);
    }

    @Transactional
    public void sendAllTheEmails(List<User> inactiveUsers) {
        for (User user : inactiveUsers){
            emailService.sendEmail("Finance Tracker Inactivity", user.getEmail(),
                    "Hey, we see you've been inactive for over a month now, would you like to give us another chance?",
                    false, null, null);
            user.setLastEmailSentOn(LocalDate.now());
            userRepository.save(user);
        }
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


}
