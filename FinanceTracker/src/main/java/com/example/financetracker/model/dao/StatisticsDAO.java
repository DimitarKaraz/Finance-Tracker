package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersRequestDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.pojo.*;
import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Component
public class StatisticsDAO {

    private final String recurrentTransactionSQL = "SELECT rt.recurrent_transaction_id, rt.name, rt.amount, rt.start_date, rt.interval_count, rt.end_date, rt.remaining_payments,\n" +
            "i.interval_id, i.days, i.name,\n" +
            "a.name, curr.currency_id, curr.name, curr.abbreviation,\n" +
            "c.category_id, c.user_id, c.name, ci.category_icon_id, ci.image_url,\n" +
            "tt.transaction_type_id, tt.name,\n" +
            "p.payment_method_id, p.name\n" +
            "FROM recurrent_transactions AS rt\n" +
            "JOIN categories AS c ON (rt.category_id = c.category_id)\n" +
            "JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)\n" +
            "JOIN transaction_types AS tt ON (rt.transaction_type_id = tt.transaction_type_id)\n" +
            "JOIN payment_methods AS p ON (rt.payment_method_id = p.payment_method_id)\n" +
            "JOIN accounts AS a ON (rt.account_id = a.account_id)\n" +
            "JOIN currencies AS curr ON (curr.currency_id = a.account_id)\n" +
            "JOIN intervals AS i ON (rt.interval_id = i.interval_id)\n" +
            "WHERE\t";
    private final String budgetSQL = "SELECT b.budget_id, b.name, b.amount_spent, b.max_limit, b.start_date, b.note, b.end_date,\n" +
            "i.interval_id, i.days, i.name,\n" +
            "a.name, curr.currency_id, curr.name, curr.abbreviation,\n" +
            "c.category_id, c.user_id, c.name, tt.transaction_type_id, tt.name, ci.category_icon_id, ci.image_url\n" +
            "FROM budgets AS b\n" +
            "JOIN budgets_have_categories AS bhc ON (b.budget_id = bhc.budget_id)\n" +
            "JOIN categories AS c ON (c.category_id = bhc.category_id)\n" +
            "JOIN transaction_types AS tt ON (c.transaction_type_id = tt.transaction_type_id)\n" +
            "JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)\n" +
            "JOIN accounts AS a ON (b.account_id = a.account_id)\n" +
            "JOIN currencies AS curr ON (curr.currency_id = a.account_id)\n" +
            "LEFT JOIN intervals AS i ON (b.interval_id = i.interval_id)\n" +
            "WHERE\t";

    private final String transactionSQL = "SELECT t.transaction_id, t.amount, t.date_time, a.name, curr.currency_id, curr.abbreviation, curr.name, tt.transaction_type_id, tt.name,\n" +
            "c.category_id, c.name, c.user_id, ci.category_icon_id, ci.image_url, p.payment_method_id, p.name\n" +
            "FROM transactions AS t\n" +
            "JOIN accounts AS a\n" +
            "ON (a.account_id = t.account_id)\n" +
            "JOIN currencies AS curr\n" +
            "ON (curr.currency_id = a.currency_id)\n" +
            "JOIN transaction_types AS tt\n" +
            "ON (tt.transaction_type_id = t.transaction_type_id)\n" +
            "JOIN categories AS c\n" +
            "ON (c.category_id = t.category_id)\n" +
            "JOIN category_icons AS ci\n" +
            "ON (ci.category_icon_id = c.category_icon_id)\n" +
            "JOIN payment_methods AS p\n" +
            "ON (p.payment_method_id = t.payment_method_id)\n" +
            "WHERE\t";

    private final String closedBudgetSQL = "SELECT b.closed_budget_id, b.name, b.amount_spent, b.max_limit, b.start_date, b.note, b.end_date,\n" +
            "i.interval_id, i.days, i.name,\n" +
            "a.name, curr.currency_id, curr.name, curr.abbreviation,\n" +
            "c.category_id, c.user_id, c.name, tt.transaction_type_id, tt.name, ci.category_icon_id, ci.image_url\n" +
            "FROM closed_budgets AS b\n" +
            "JOIN closed_budgets_have_categories AS bhc ON (b.closed_budget_id = bhc.closed_budget_id)\n" +
            "JOIN categories AS c ON (c.category_id = bhc.category_id)\n" +
            "JOIN transaction_types AS tt ON (c.transaction_type_id = tt.transaction_type_id)\n" +
            "JOIN category_icons AS ci ON (c.category_icon_id = ci.category_icon_id)\n" +
            "JOIN accounts AS a ON (b.account_id = a.account_id)\n" +
            "JOIN currencies AS curr ON (curr.currency_id = a.account_id)\n" +
            "LEFT JOIN intervals AS i ON (b.interval_id = i.interval_id)\n" +
            "WHERE\t";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<RecurrentTransactionResponseDTO> getRecurrentTransactionsByFilters(RecurrentTransactionByFiltersRequestDTO filtersDTO) {
        String sql = generateRecurrentTransactionSQLQuery(filtersDTO);

        return jdbcTemplate.query(sql, new ResultSetExtractor<List<RecurrentTransactionResponseDTO>>() {
            @Override
            public List<RecurrentTransactionResponseDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<RecurrentTransactionResponseDTO> recurrentTransactions = new ArrayList<>();

                while (rs.next()) {
                    recurrentTransactions.add(buildRecurrentTransactionResponseDTO(rs));
                }
                return recurrentTransactions;
            }
        });
    }

    public List<TransactionResponseDTO> getTransactionsByFilters(TransactionByFiltersRequestDTO requestDTO){
        String sql = generateTransactionSQLQuery(requestDTO);

        return jdbcTemplate.query(sql, new ResultSetExtractor<List<TransactionResponseDTO>>() {
            @Override
            public List<TransactionResponseDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<TransactionResponseDTO> transactions = new ArrayList<>();

                while (rs.next()) {
                    transactions.add(buildTransactionResponseDTO(rs));
                }
                return transactions;
            }
        });
    }

    public List<BudgetResponseDTO> getBudgetsByFilters(BudgetByFiltersRequestDTO filtersDTO) {
        String sql = generateBudgetSQLQuery(filtersDTO);

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

    public List<ClosedBudgetResponseDTO> getClosedBudgetsByFilters(BudgetByFiltersRequestDTO filtersDTO) {
        String sql = generateClosedBudgetSQLQuery(filtersDTO);

        return jdbcTemplate.query(sql,
                new ResultSetExtractor<List<ClosedBudgetResponseDTO>>() {
                    @Override
                    public List<ClosedBudgetResponseDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        LinkedList<ClosedBudgetResponseDTO> budgets = new LinkedList<>();
                        int tempBudgetId = -1;

                        while (rs.next()) {
                            if (tempBudgetId != rs.getInt("b.closed_budget_id")) {
                                tempBudgetId = rs.getInt("b.closed_budget_id");
                                budgets.add(buildClosedBudgetResponseDTO(rs));
                            }
                            budgets.getLast().getCategoryResponseDTOs().add(buildCategoryResponseDTO(rs));
                        }
                        return budgets;
                    }
                });
    }

//  Generate SQL methods:

    private String generateRecurrentTransactionSQLQuery(RecurrentTransactionByFiltersRequestDTO filtersDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = recurrentTransactionSQL +
                "(a.user_id = " + userId + ") AND (rt.start_date BETWEEN \"" +
                filtersDTO.getStartDate() + "\" AND \"" + filtersDTO.getEndDate() + "\")\n";
        if (filtersDTO.getAccountId() != null) {
            sql += "AND (rt.account_id = " + filtersDTO.getAccountId() + ")\n";
        }
        if (filtersDTO.getIntervalId() != null) {
            sql += "AND (rt.interval_id = " + filtersDTO.getIntervalId() + ")\n";
        }
        if (filtersDTO.getTransactionTypeId() != null) {
            sql += "AND (rt.transaction_type_id = " + filtersDTO.getTransactionTypeId() + ")\n";
        }
        if (filtersDTO.getCategoryIds() != null) {
            StringBuffer categoryIds = new StringBuffer();
            filtersDTO.getCategoryIds()
                    .forEach(integer -> categoryIds.append(integer).append(","));
            categoryIds.deleteCharAt(categoryIds.length() - 1);

            sql += "AND (rt.category_id IN (" +
                    categoryIds +
                    "))\n";
        }
        if (filtersDTO.getPaymentMethodId() != null) {
            sql += "AND (rt.payment_method_id = " + filtersDTO.getPaymentMethodId() + ")\n";
        }
        if (filtersDTO.getAmountMin() != null || filtersDTO.getAmountMax() != null) {
            sql += "AND (rt.amount BETWEEN " +
                    (filtersDTO.getAmountMin() != null ? filtersDTO.getAmountMin() : BigDecimal.valueOf(0.00)) +
                    " AND " +
                    (filtersDTO.getAmountMax() != null ? filtersDTO.getAmountMax() : BigDecimal.valueOf(9999999999999.99)) +
                    ")\n";
        }
        sql += "ORDER BY rt.interval_id ASC;";
        return sql;
    }

    private String generateTransactionSQLQuery(TransactionByFiltersRequestDTO requestDTO){
        int userId = MyUserDetailsService.getCurrentUserId();
        String sql = transactionSQL +
                "(a.user_id = " + userId + ") AND (t.date_time BETWEEN \"" +
                requestDTO.getStartDate().atTime(LocalTime.MIN) + "\" AND \"" + requestDTO.getEndDate().atTime(LocalTime.MAX) + "\")\n";
        if (requestDTO.getAccountId() != null) {
            sql += "AND (t.account_id = " + requestDTO.getAccountId() + ")\n";
        }
        if (requestDTO.getTransactionTypeId() != null) {
            sql += "AND (t.transaction_type_id = " + requestDTO.getTransactionTypeId() + ")\n";
        }
        if (requestDTO.getCategoryIds() != null) {
            StringBuffer categoryIds = new StringBuffer();
            requestDTO.getCategoryIds()
                    .forEach(integer -> categoryIds.append(integer).append(","));
            categoryIds.deleteCharAt(categoryIds.length() - 1);

            sql += "AND (t.category_id IN (" +
                    categoryIds +
                    "))\n";
        }
        if (requestDTO.getPaymentMethodId() != null) {
            sql += "AND (t.payment_method_id = " + requestDTO.getPaymentMethodId() + ")\n";
        }
        if (requestDTO.getAmountMin() != null || requestDTO.getAmountMax() != null) {
            sql += "AND (t.amount BETWEEN " +
                    (requestDTO.getAmountMin() != null ? requestDTO.getAmountMin() : BigDecimal.valueOf(0.00)) +
                    " AND " +
                    (requestDTO.getAmountMax() != null ? requestDTO.getAmountMax() : BigDecimal.valueOf(9999999999999.99)) +
                    ")\n";
        }
        sql += "ORDER BY t.date_time ASC;";
        return sql;
    }

    private String generateBudgetSQLQuery(BudgetByFiltersRequestDTO filtersDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = budgetSQL +
                "(a.user_id = " + userId + ") AND (b.start_date BETWEEN \"" +
                filtersDTO.getStartDate() + "\" AND \"" + filtersDTO.getEndDate() + "\")\n";
        if (filtersDTO.getAccountId() != null) {
            sql += "AND (b.account_id = " + filtersDTO.getAccountId() + ")\n";
        }
        if (filtersDTO.getIntervalId() != null) {
            sql += "AND (b.interval_id = " + filtersDTO.getIntervalId() + ")\n";
        }
        if (filtersDTO.getCategoryIds() != null) {
            StringBuffer categoryIds = new StringBuffer();
            filtersDTO.getCategoryIds()
                    .forEach(integer -> categoryIds.append(integer).append(","));
            categoryIds.deleteCharAt(categoryIds.length() - 1);

            sql += "AND (bhc.category_id IN (" + categoryIds + "))\n";
        }
        if (filtersDTO.getAmountMin() != null || filtersDTO.getAmountMax() != null) {
            sql += "AND (b.max_limit BETWEEN " +
                    (filtersDTO.getAmountMin() != null ? filtersDTO.getAmountMin() : BigDecimal.valueOf(0.00)) +
                    " AND " +
                    (filtersDTO.getAmountMax() != null ? filtersDTO.getAmountMax() : BigDecimal.valueOf(9999999999999.99)) +
                    ")\n";
        }
        sql += "ORDER BY b.budget_id ASC;";
        return sql;
    }

    private String generateClosedBudgetSQLQuery(BudgetByFiltersRequestDTO filtersDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = closedBudgetSQL +
                "(a.user_id = " + userId + ") AND (b.start_date BETWEEN \"" +
                filtersDTO.getStartDate() + "\" AND \"" + filtersDTO.getEndDate() + "\")\n";
        if (filtersDTO.getAccountId() != null) {
            sql += "AND (b.account_id = " + filtersDTO.getAccountId() + ")\n";
        }
        if (filtersDTO.getIntervalId() != null) {
            sql += "AND (b.interval_id = " + filtersDTO.getIntervalId() + ")\n";
        }
        if (filtersDTO.getCategoryIds() != null) {
            StringBuffer categoryIds = new StringBuffer();
            filtersDTO.getCategoryIds()
                    .forEach(integer -> categoryIds.append(integer).append(","));
            categoryIds.deleteCharAt(categoryIds.length() - 1);

            sql += "AND (bhc.category_id IN (" + categoryIds + "))\n";
        }
        if (filtersDTO.getAmountMin() != null || filtersDTO.getAmountMax() != null) {
            sql += "AND (b.max_limit BETWEEN " +
                    (filtersDTO.getAmountMin() != null ? filtersDTO.getAmountMin() : BigDecimal.valueOf(0.00)) +
                    " AND " +
                    (filtersDTO.getAmountMax() != null ? filtersDTO.getAmountMax() : BigDecimal.valueOf(9999999999999.99)) +
                    ")\n";
        }
        sql += "ORDER BY b.closed_budget_id ASC;";
        return sql;
    }

//  Builder methods:

    private TransactionResponseDTO buildTransactionResponseDTO(ResultSet rs) throws SQLException {
        return TransactionResponseDTO.builder()
                .transactionId(rs.getInt("t.transaction_id"))
                .accountName(rs.getString("a.name"))
                .currency(buildCurrency(rs))
                .amount(rs.getBigDecimal("t.amount"))
                .transactionType(buildTransactionType(rs))
                .categoryResponseDTO(buildCategoryResponseDTO(rs))
                .paymentMethod(buildPaymentMethod(rs))
                .dateTime(rs.getTimestamp("t.date_time").toLocalDateTime())
                .build();
    }

    private RecurrentTransactionResponseDTO buildRecurrentTransactionResponseDTO(ResultSet rs) throws SQLException {
        return RecurrentTransactionResponseDTO.builder()
                .recurrentTransactionId(rs.getInt("rt.recurrent_transaction_id"))
                .name(rs.getString("rt.name"))
                .accountName(rs.getString("a.name"))
                .currency(buildCurrency(rs))
                .amount(rs.getBigDecimal("rt.amount"))
                .transactionType(buildTransactionType(rs))
                .categoryResponseDTO(buildCategoryResponseDTO(rs))
                .paymentMethod(buildPaymentMethod(rs))
                .startDate(rs.getDate("rt.start_date").toLocalDate())
                .interval(buildInterval(rs))
                .intervalCount(rs.getInt("rt.interval_count"))
                .endDate(rs.getDate("rt.end_date") != null ? rs.getDate("rt.end_date").toLocalDate() : null)
                .remainingPayments(rs.getInt("rt.remaining_payments") != 0 ? rs.getInt("rt.remaining_payments") : null)
                .build();
    }

    private BudgetResponseDTO buildBudgetResponseDTO(ResultSet rs) throws SQLException {
        return BudgetResponseDTO.builder()
                .budgetId(rs.getInt("b.budget_id"))
                .name(rs.getString("b.name"))
                .amountSpent(rs.getBigDecimal("b.amount_spent"))
                .maxLimit(rs.getBigDecimal("b.max_limit"))
                .interval(buildInterval(rs))
                .startDate(rs.getDate("b.start_date").toLocalDate())
                .accountName(rs.getString("a.name"))
                .currency(buildCurrency(rs))
                .note(rs.getString("b.note"))
                .categoryResponseDTOs(new HashSet<>())
                .endDate(rs.getDate("b.end_date") != null ? rs.getDate("b.end_date").toLocalDate() : null)
                .build();
    }

    private ClosedBudgetResponseDTO buildClosedBudgetResponseDTO(ResultSet rs) throws SQLException {
        return ClosedBudgetResponseDTO.builder()
                .closedBudgetId(rs.getInt("b.closed_budget_id"))
                .name(rs.getString("b.name"))
                .amountSpent(rs.getBigDecimal("b.amount_spent"))
                .maxLimit(rs.getBigDecimal("b.max_limit"))
                .interval(buildInterval(rs))
                .startDate(rs.getDate("b.start_date").toLocalDate())
                .accountName(rs.getString("a.name"))
                .currency(buildCurrency(rs))
                .note(rs.getString("b.note"))
                .categoryResponseDTOs(new HashSet<>())
                .endDate(rs.getDate("b.end_date") != null ? rs.getDate("b.end_date").toLocalDate() : null)
                .build();
    }

    private CategoryResponseDTO buildCategoryResponseDTO(ResultSet rs) throws SQLException {
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .categoryIconId(rs.getInt("ci.category_icon_id"))
                .imageUrl(rs.getString("ci.image_url"))
                .build();

        return CategoryResponseDTO.builder()
                .categoryId(rs.getInt("c.category_id"))
                .userId(rs.getInt("c.user_id") != 0 ? rs.getInt("c.user_id") : null)
                .name(rs.getString("c.name"))
                .transactionType(buildTransactionType(rs))
                .categoryIcon(categoryIcon)
                .build();
    }

    private TransactionType buildTransactionType(ResultSet rs) throws SQLException {
        return TransactionType.builder()
                .transactionTypeId(rs.getInt("tt.transaction_type_id"))
                .name(rs.getString("tt.name"))
                .build();
    }

    private Currency buildCurrency(ResultSet rs) throws SQLException {
        return Currency.builder()
                .currencyId(rs.getInt("curr.currency_id"))
                .name(rs.getString("curr.name"))
                .abbreviation(rs.getString("curr.abbreviation"))
                .build();
    }

    private Interval buildInterval(ResultSet rs) throws SQLException {
        if (rs.getInt("i.interval_id") == 0) {
            return null;
        }
        return Interval.builder()
                .intervalId(rs.getInt("i.interval_id"))
                .days(rs.getInt("i.days"))
                .name(rs.getString("i.name"))
                .build();
    }

    private PaymentMethod buildPaymentMethod(ResultSet rs) throws SQLException {
        return PaymentMethod.builder()
                .paymentMethodId(rs.getInt("p.payment_method_id"))
                .name(rs.getString("p.name"))
                .build();
    }





}
