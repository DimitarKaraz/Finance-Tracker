package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    boolean existsByAccount_AccountIdAndName(int accountId, String name);

    List<Budget> findAllByAccount_User_UserId(int userId);
}
