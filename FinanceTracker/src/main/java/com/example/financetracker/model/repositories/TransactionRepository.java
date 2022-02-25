package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {


    List<Transaction> findAllByCategoryCategoryId(int categoryId);

    List<Transaction> findAllByAccount_User_UserId(int userId);

    List<Transaction> findAllByAccount_AccountId(int accountId);

    List<Transaction> findTransactionsByCategoryIsInAndAccountUserUserId(Set<Category> categories, int userId);

    List<Transaction> findTransactionsByDateTimeAfter(LocalDateTime localDateTime);

    List<Transaction> findAllByAmountBetween(BigDecimal min, BigDecimal max);

    List<Transaction> findAllByCategory_CategoryIdIsIn(Set<Integer> categoryIds);

    List<Transaction> findAllByTransactionType_TransactionTypeId(int transactionTypeId);

    List<Transaction> findAllByPaymentMethod_PaymentMethodId(int paymentMethodId);


}
