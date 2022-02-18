package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

//    @Modifying
//    @Query(value = "INSERT INTO transactions\n" +
//                    "(transaction_type_id, amount, account_id, category_id, payment_method_id, date_time)\n" +
//                    "VALUES\n" +
//                    "(?, ?, ?, ?, ?, ?);", nativeQuery = true)
//    void insertIntoDB(int transactionTypeId, BigDecimal amount, int accountId,
//                             int categoryId, int paymentMethodId, LocalDateTime dateTime);

    List<Transaction> findAllByCategoryCategoryId(int categoryId);

    List<Transaction> findAllByAccount_User_UserId(int userId);

    List<Transaction> findAllByAccount_AccountId(int accountId);

    List<Transaction> findTransactionsByCategoryIsInAndAccountUserUserId(Set<Category> categories, int userId);

    List<Transaction> findTransactionsByDateTimeAfter(LocalDateTime localDateTime);


}
