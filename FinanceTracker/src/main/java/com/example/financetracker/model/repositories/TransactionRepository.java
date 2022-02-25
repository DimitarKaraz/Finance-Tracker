package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {


    List<Transaction> findAllByCategoryCategoryId(int categoryId);

//    List<Transaction> findAllByAccount_User_UserId(int userId);

    Page<Transaction> findAllByAccount_User_UserId(int userId, Pageable pageable);


    List<Transaction> findAllByAccount_AccountId(int accountId);



}
