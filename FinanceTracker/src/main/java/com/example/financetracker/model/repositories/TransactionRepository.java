package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Modifying
    @Query(value = "INSERT INTO transactions\n" +
                    "(transaction_type_id, amount, account_id, category_id, payment_method_id, date_time)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?, ?);", nativeQuery = true)
    void insertIntoDB(int transactionTypeId, BigDecimal amount, int accountId,
                             int categoryId, int paymentMethodId, LocalDateTime dateTime);


}
