package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.ClosedBudget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ClosedBudgetRepository extends JpaRepository<ClosedBudget, Integer> {

    Page<ClosedBudget> findAllByAccount_User_UserId(int userId, Pageable pageable);

    @Query(value = "SELECT cb.* " +
            "FROM closed_budgets AS cb\n" +
            "JOIN closed_budgets_have_categories AS cbhc\n" +
            "ON (cb.closed_budget_id = cbhc.closed_budget_id)\n" +
            "WHERE category_id = ?;", nativeQuery = true)
    Set<ClosedBudget> findAllByCategoryId(int categoryId);

}
