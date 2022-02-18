package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.ClosedBudget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClosedBudgetRepository extends JpaRepository<ClosedBudget, Integer> {

}
