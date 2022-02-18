package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.ClosedBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosedBudgetRepository extends JpaRepository<ClosedBudget, Integer> {

    List<ClosedBudget> findAllByAccount_User_UserId(int userId);

}
