package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findAllByAccount_User_UserId(int userId);

    List<Transaction> findAllByAccount_AccountId(int accountId);
}
