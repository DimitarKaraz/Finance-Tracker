package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class StatisticsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired

    private String generateBudgetSQL(@org.jetbrains.annotations.NotNull BudgetByFiltersDTO filtersDTO) {
        String sql = "SELECT b.budget_id, b.name, b.amount_spent, b.max_limit, b.start_date, b.note, b.end_date,\n" +
                    "    a.account_name, curr.currency_id, curr.name, curr.abbreviation,\n" +
                    "    c.category_id, c.user_id, c.name, tr.transaction_type_id, tr.name, ci.category_icon_id, ci.image_url\n" +
                    "    FROM budgets AS b\n" +
                    "    JOIN budgets_have_categories AS bhc ON (b.budget_id = bhc.budget_id)\n" +
                    "    JOIN categories AS c ON (c.category_id = bhc.category_id)\n" +
                    "    JOIN transaction_types AS tr ON (c.transaction_type_id = tr.transaction_type_id)\n" +
                    "    JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)\n" +
                    "    JOIN accounts AS a ON (b.account_id = a.account_id)\n" +
                    "    JOIN currencies AS curr ON (curr.currency_id = a.account_id)\n" +
                    "    LEFT JOIN intervals AS i ON (b.interval_id = i.interval_id)\n" +
                    "    WHERE (b.start_date BETWEEN \"" +
                filtersDTO.getStartDate() + "\" AND \"" +
                filtersDTO.getEndDate() +
                "\")\n";
        if (filtersDTO.getAccountId() != null) {
            sql += "AND (b.account_id = " +
                    filtersDTO.getAccountId() +
                    ")\n";
        }
        if (filtersDTO.getIntervalId() != null) {
            sql += "AND (b.interval_id = " +
                    filtersDTO.getIntervalId() +
                    ")\n";
        }
        if (filtersDTO.getCategoryIds() != null) {
            StringBuffer categoryIds = new StringBuffer();
            filtersDTO.getCategoryIds()
                    .forEach(integer -> categoryIds.append(integer).append(","));
            categoryIds.deleteCharAt(categoryIds.length() - 1);

            sql += "AND (bhc.category_id IN (" +
                    categoryIds +
                    "))\n";
        }
        if (filtersDTO.getAmountMin() != null || filtersDTO.getAmountMax() != null) {
            sql += "AND (b.max_limit BETWEEN " +
                    (filtersDTO.getAmountMin() != null ? filtersDTO.getAmountMin() : "0.00") +
                    " AND " +
                    (filtersDTO.getAmountMax() != null ? filtersDTO.getAmountMax() : "9999999999999.99") +
                    ");";
        }
        return sql;
    }

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        String sql = generateBudgetSQL(filtersDTO);
        System.out.println("*************** \n " + sql);

        List<BudgetResponseDTO> budgetsFound = jdbcTemplate.query(sql,
                new ResultSetExtractor<List<BudgetResponseDTO>>() {
                    @Override
                    public List<BudgetResponseDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        LinkedList<BudgetResponseDTO> budgets = new LinkedList<>();

                        Set<CategoryResponseDTO> tempCategories = new HashSet<>();
                        int tempBudgetId = -1;
                        while (rs.next()) {
                            if (tempBudgetId != rs.getInt("b.budget_id")) {
                                tempBudgetId = rs.getInt("b.budget_id");
                                tempCategories = new HashSet<>();


                            } else {

                                TransactionType transactionType = TransactionType.builder()
                                        .transactionTypeId(rs.getInt("tr.transaction_type_id"))
                                        .name(rs.getString("tr.name"))
                                        .build();
                                CategoryIcon categoryIcon = CategoryIcon.builder()
                                        .categoryIconId(rs.getInt("ci.category_icon_id"))
                                        .imageUrl(rs.getString("ci.image_url"))
                                        .build();

                                budgets.getLast().getCategoryResponseDTOs().add(
                                        CategoryResponseDTO.builder()
                                                    .categoryId(rs.getInt("c.category_id"))
                                                    .userId(rs.getInt("c.user_id"))
                                                    .name(rs.getString("c.name"))
                                                    .transactionType(transactionType)
                                                    .categoryIcon(categoryIcon)
                                                    .build()
                                );

                            }
                        }

                    }
                });


        LinkedList<Budget> budgets = new LinkedList<>();
        Set<Category> tempCategories = new HashSet<>();
        int tempBudgetId = -1;
        ResultSet rs = con.prepareStatement("SELECT....");
        while (rs.next()) {
            if (tempBudgetId == rs.findColumn("budgetId")) {
                tempCategories.add(new Category(rs.findColumn("categoryId"), rs.findColumn("c.name")...);
                budgets.getLast().setCategories(tempCategories);
            } else {
                tempBudgetId = rs.findColumn("budgetId");
                tempCategories = new HashSet<>();
                tempCategories.add(new Category(rs.findColumn("categoryId"), rs.findColumn("c.name")...);
                budgets.add(Budget.builder()
                        .budgetId(tempBudgetId)
                        .name(rs.getString("name"))
                        .categories(tempCategories)
                        .X ....
                        .build;
            }
        }






        return budgetsFound;
    }

    private


    /*              MASTER QUERY:

    SELECT b.budget_id, b.name, b.amount_spent, b.max_limit, b.start_date, b.note, b.end_date,
    a.account_name, curr.currency_id, curr.name, curr.abbreviation,
    c.category_id, c.user_id, c.name, tr.transaction_type_id, tr.name, ci.category_icon_id, ci.image_url
    FROM budgets AS b
    JOIN budgets_have_categories AS bhc ON (b.budget_id = bhc.budget_id)
    JOIN categories AS c ON (c.category_id = bhc.category_id)
    JOIN transaction_types AS tr ON (c.transaction_type_id = tr.transaction_type_id)
    JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)
    JOIN accounts AS a ON (b.account_id = a.account_id)
    JOIN currencies AS curr ON (curr.currency_id = a.account_id)
    LEFT JOIN intervals AS i ON (b.interval_id = i.interval_id)
    WHERE ...
    ORDER BY b.budget_id ASC;

     */


}
