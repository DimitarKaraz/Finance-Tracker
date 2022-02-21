package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.CategoryIcon;
import com.example.financetracker.model.pojo.Currency;
import com.example.financetracker.model.pojo.Interval;
import com.example.financetracker.model.pojo.TransactionType;
import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Component
public class StatisticsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        String sql = generateBudgetSQL(filtersDTO);

        return jdbcTemplate.query(sql,
                new ResultSetExtractor<List<BudgetResponseDTO>>() {
                    @Override
                    public List<BudgetResponseDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        LinkedList<BudgetResponseDTO> budgets = new LinkedList<>();
                        int tempBudgetId = -1;

                        while (rs.next()) {
                            if (tempBudgetId != rs.getInt("b.budget_id")) {
                                tempBudgetId = rs.getInt("b.budget_id");
                                budgets.add(buildBudgetResponseDTO(rs));
                            }
                            budgets.getLast().getCategoryResponseDTOs().add(buildCategoryResponseDTO(rs));
                        }
                        return budgets;
                    }
                });
    }

    private String generateBudgetSQL(BudgetByFiltersDTO filtersDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = "SELECT b.budget_id, b.name, b.amount_spent, b.max_limit, b.start_date, b.note, b.end_date,\n" +
                "i.interval_id, i.days, i.name,\n" +
                "a.name, curr.currency_id, curr.name, curr.abbreviation,\n" +
                "c.category_id, c.user_id, c.name, tr.transaction_type_id, tr.name, ci.category_icon_id, ci.image_url\n" +
                "FROM budgets AS b\n" +
                "JOIN budgets_have_categories AS bhc ON (b.budget_id = bhc.budget_id)\n" +
                "JOIN categories AS c ON (c.category_id = bhc.category_id)\n" +
                "JOIN transaction_types AS tr ON (c.transaction_type_id = tr.transaction_type_id)\n" +
                "JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)\n" +
                "JOIN accounts AS a ON (b.account_id = a.account_id)\n" +
                "JOIN currencies AS curr ON (curr.currency_id = a.account_id)\n" +
                "LEFT JOIN intervals AS i ON (b.interval_id = i.interval_id)\n" +
                "WHERE (a.user_id = " + userId +
                ") AND (b.start_date BETWEEN \"" +
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
                    (filtersDTO.getAmountMin() != null ? filtersDTO.getAmountMin() : BigDecimal.valueOf(0.00)) +
                    " AND " +
                    (filtersDTO.getAmountMax() != null ? filtersDTO.getAmountMax() : BigDecimal.valueOf(9999999999999.99)) +
                    ");";
        }
        return sql;
    }

    private CategoryResponseDTO buildCategoryResponseDTO(ResultSet rs) throws SQLException {
        TransactionType transactionType = TransactionType.builder()
                .transactionTypeId(rs.getInt("tr.transaction_type_id"))
                .name(rs.getString("tr.name"))
                .build();
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .categoryIconId(rs.getInt("ci.category_icon_id"))
                .imageUrl(rs.getString("ci.image_url"))
                .build();

        return CategoryResponseDTO.builder()
                .categoryId(rs.getInt("c.category_id"))
                .userId(rs.getInt("c.user_id"))
                .name(rs.getString("c.name"))
                .transactionType(transactionType)
                .categoryIcon(categoryIcon)
                .build();
    }

    private BudgetResponseDTO buildBudgetResponseDTO(ResultSet rs) throws SQLException {
        Interval interval = Interval.builder()
                .intervalId(rs.getInt("i.interval_id"))
                .days(rs.getInt("i.days"))
                .name(rs.getString("i.name"))
                .build();
        Currency currency= Currency.builder()
                .currencyId(rs.getInt("curr.currency_id"))
                .name(rs.getString("curr.name"))
                .abbreviation(rs.getString("curr.abbreviation"))
                .build();

        return BudgetResponseDTO.builder()
                .budgetId(rs.getInt("b.budget_id"))
                .name(rs.getString("b.name"))
                .amountSpent(rs.getBigDecimal("b.amount_spent"))
                .maxLimit(rs.getBigDecimal("b.max_limit"))
                .interval(interval)
                .startDate(rs.getDate("b.start_date").toLocalDate())
                .accountName(rs.getString("a.name"))
                .currency(currency)
                .note(rs.getString("b.note"))
                .categoryResponseDTOs(new HashSet<>())
                .endDate(rs.getDate("b.end_date") != null ? rs.getDate("b.end_date").toLocalDate() : null)
                .build();
    }


}
