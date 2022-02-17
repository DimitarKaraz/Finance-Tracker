package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    boolean existsByAccount_AccountIdAndName(int accountId, String name);

    List<Budget> findAllByAccount_User_UserId(int userId);

    @Modifying
    @Query(value = "SELECT budgets.budget_id, name, amount_spent, max_limit, interval_id, start_date, account_id, note, end_date FROM budgets\n" +
            "JOIN budgets_have_categories\n" +
            "ON (budgets.budget_id = budgets_have_categories.budget_id)\n" +
            "WHERE account_id = ? AND category_id = ?;", nativeQuery = true)
    Set<Budget> findAllByCategoryIdAndAccountId(int accountId, int categoryId);

    @Modifying
    @Query(value = "SELECT budgets.budget_id, name, amount_spent, max_limit, interval_id, start_date, account_id, note, end_date FROM budgets\n" +
            "JOIN budgets_have_categories\n" +
            "ON (budgets.budget_id = budgets_have_categories.budget_id)\n" +
            "WHERE category_id = ?;", nativeQuery = true)
    Set<Budget> findAllByCategoryId(int categoryId);
}
