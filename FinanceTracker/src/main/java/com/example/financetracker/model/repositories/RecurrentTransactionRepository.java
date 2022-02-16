package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.RecurrentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrentTransactionRepository extends JpaRepository<RecurrentTransaction, Integer> {


}
