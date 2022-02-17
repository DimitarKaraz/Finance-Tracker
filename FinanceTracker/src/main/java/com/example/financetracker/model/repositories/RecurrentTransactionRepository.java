package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.RecurrentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurrentTransactionRepository extends JpaRepository<RecurrentTransaction, Integer> {

    List<RecurrentTransaction> findAllByAccount_User_UserId(int userId);

    List<RecurrentTransaction> findAllByCategoryCategoryId(int categoryId);
}
