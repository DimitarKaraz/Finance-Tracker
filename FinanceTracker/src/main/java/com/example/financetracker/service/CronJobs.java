package com.example.financetracker.service;

import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.RecurrentTransactionRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import com.example.financetracker.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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
import java.util.Properties;

@Component
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CronJobs {
    public static final String SENDER_MAIL = "plevenskikozi@gmail.com";
    public static final String HOST = "smtp.gmail.com";
    public static final String PASSWORD = "financetrackerpass";

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

    @Scheduled(cron = "0 0 0 * * *")
    @Retryable( value = Exception.class,
            maxAttempts = 10, backoff = @Backoff(delay = 60*1000))
    public void recurrentCronJob(){
        List<RecurrentTransaction> allRecurrentTransactions = recurrentTransactionRepository.findAllThatExpireOrNeedPaymentToday();
        executeAllRecurrentTransactions(allRecurrentTransactions);
    }

    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    @Retryable(value = Exception.class,
            maxAttempts = 10, backoff = @Backoff(delay = 60*1000))
    public void budgetCronJob() throws Exception {
        List<Budget> budgets = budgetRepository.findAllBudgetsReadyForCronJob();
        resetAllBudgets(budgets);

    }

    @Scheduled(cron = "0 0 0 * * *")
    @Retryable(value = Exception.class,
            maxAttempts = 10, backoff = @Backoff(delay = 60*1000))
    public void sendEmailToInactiveUsersCronJob(){
        List<User> inactiveUsers = userRepository.findAllInactiveUsers();
        sendAllTheEmails(inactiveUsers);
    }

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


    public void sendAllTheEmails(List<User> inactiveUsers){
        for (User user : inactiveUsers){
            sendEmail(user.getEmail());
        }
    }

    public void sendEmail(String recipient){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_MAIL, PASSWORD);
            }
        });

        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(SENDER_MAIL));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            message.setSubject("Finance-Tracker Inactivity");

            message.setText("Hey, we see you've been inactive for over a month now, would you like to give us another chance?");

            Transport.send(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
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
