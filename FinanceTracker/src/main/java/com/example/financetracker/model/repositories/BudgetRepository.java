package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    boolean existsByAccount_AccountIdAndName(int accountId, String name);

    Page<Budget> findAllByAccount_User_UserId(int userId, Pageable pageable);

    @Query(value = "SELECT budgets.*" +
            "FROM budgets\n" +
            "JOIN budgets_have_categories\n" +
            "ON (budgets.budget_id = budgets_have_categories.budget_id)\n" +
            "WHERE account_id = ? AND category_id = ? AND start_date <= ?;", nativeQuery = true)
    Set<Budget> findAllByCategoryIdAndAccountIdAndStartDate(int accountId, int categoryId, LocalDate date);

    @Query(value = "SELECT budgets.* " +
            "FROM budgets\n" +
            "JOIN budgets_have_categories\n" +
            "ON (budgets.budget_id = budgets_have_categories.budget_id)\n" +
            "WHERE category_id = ?;", nativeQuery = true)
    Set<Budget> findAllByCategoryId(int categoryId);

    @Query(value = "SELECT b.*\n" +
            "FROM budgets AS b\n" +
            "LEFT JOIN intervals AS i ON b.interval_id = i.interval_id\n" +
            "WHERE b.end_date = CURDATE() OR DATE_ADD(b.start_date, INTERVAL i.days DAY) = CURDATE();", nativeQuery = true)
    List<Budget> findAllBudgetsReadyForCronJob();

}
