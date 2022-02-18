package com.example.financetracker.service;

import com.example.financetracker.model.pojo.RecurrentTransaction;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.repositories.RecurrentTransactionRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class CronJobs {

    @Autowired
    private RecurrentTransactionRepository recurrentTransactionRepository;
    @Autowired
    private TransactionRepository transactionRepository;

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
}
