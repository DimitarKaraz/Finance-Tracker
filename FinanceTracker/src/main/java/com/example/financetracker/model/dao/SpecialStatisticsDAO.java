package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.specialStatisticsDTOs.AverageTransactionForTransactionTypesResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.TopFiveExpensesOrIncomesResponseDTO;
import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class SpecialStatisticsDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public TopFiveExpensesOrIncomesResponseDTO getTopFiveExpensesOrIncomesByDates(FilterByDatesRequestDTO requestDTO, String transactionTypeName){
        String sql = generateTopFiveExpensesOrIncomesSQLQuery(requestDTO, transactionTypeName);

        return jdbcTemplate.query(sql,
                new ResultSetExtractor<TopFiveExpensesOrIncomesResponseDTO>() {
                    @Override
                    public TopFiveExpensesOrIncomesResponseDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                        TopFiveExpensesOrIncomesResponseDTO topFive = new TopFiveExpensesOrIncomesResponseDTO();
                        topFive.setTopFiveExpenses(new HashMap<>());
                        while (rs.next()) {
                            topFive.getTopFiveExpenses().put(rs.getString("Category"), rs.getBigDecimal("Total"));
                        }
                        return topFive;
                    }
                });
    }

    private String generateTopFiveExpensesOrIncomesSQLQuery(FilterByDatesRequestDTO requestDTO, String transactionTypeName) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = "SELECT c.name as \"Category\", SUM(t.amount) AS Total\n" +
                "FROM transactions AS t\n" +
                "JOIN accounts as a\n" +
                "ON (t.account_id = a.account_id)\n" +
                "JOIN categories AS c\n" +
                "ON (t.category_id = c.category_id)\n" +
                "JOIN transaction_types as tt\n" +
                "ON (t.transaction_type_id = tt.transaction_type_id)\n" +
                "WHERE (LOWER(tt.name) ";

        if (transactionTypeName.equalsIgnoreCase("expense")){
            sql += "= \"expense\")";
        }
        if (transactionTypeName.equalsIgnoreCase("income")){
            sql += "= \"income\")";
        }

        sql += "AND (a.user_id = " + userId + ") AND (t.start_date BETWEEN \"" +
                requestDTO.getStartDate() + "\" AND \"" + requestDTO.getEndDate() + "\")\n";

        sql += "GROUP BY t.category_id\n" +
                "ORDER BY Total DESC\n" +
                "LIMIT 5";
        return sql;
    }

    public CashFlowsResponseDTO getCashFlowsForAccounts(FilterByDatesRequestDTO requestDTO) {
        final String sql = generateCashFlowSQLQuery(requestDTO);

        return jdbcTemplate.query(sql, new ResultSetExtractor<CashFlowsResponseDTO>() {
            @Override
            public CashFlowsResponseDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, Map<String, Integer>> accountCashFlows = new LinkedHashMap<>();

                while (rs.next()) {
                    String accountName = rs.getString("accountName") + "(" + rs.getString("currency") + ")";
                    accountCashFlows.putIfAbsent(accountName, new TreeMap<>());
                    accountCashFlows.get(accountName).put("Total income", rs.getInt("totalIncome"));
                    accountCashFlows.get(accountName).put("Total expenses", rs.getInt("totalExpenses"));
                }
                CashFlowsResponseDTO responseDTO = new CashFlowsResponseDTO();
                responseDTO.setCashFlowsForAccounts(accountCashFlows);
                return responseDTO;
            }
        });
    }

    private String generateCashFlowSQLQuery(FilterByDatesRequestDTO requestDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = "SELECT SUM((LOWER(tt.name) = 'income') * amount) AS totalIncome,\n" +
                "SUM((LOWER(tt.name) = 'expense') * amount) AS totalExpenses,\n" +
                "a.name AS accountName, curr.abbreviation AS currency\n" +
                "FROM transactions AS t\n" +
                "JOIN transaction_types AS tt ON (t.transaction_type_id = tt.transaction_type_id)\n" +
                "JOIN accounts AS a ON (t.account_id = a.account_id)\n" +
                "JOIN currencies AS curr ON (curr.currency_id = a.currency_id)\n" +
                "WHERE (a.user_id = " + userId + ") AND (t.date_time BETWEEN \"" +
                requestDTO.getStartDate() + "\" AND \"" + requestDTO.getEndDate() + "\")\n" +
                "GROUP BY t.account_id\n" +
                "ORDER BY a.name ASC;";

        return sql;
    }

    public AverageTransactionForTransactionTypesResponseDTO getAverageTransactions(FilterByDatesRequestDTO requestDTO) {
        final String sql = generateAverageTransactionSQLQuery(requestDTO);

        return jdbcTemplate.query(sql, new ResultSetExtractor<AverageTransactionForTransactionTypesResponseDTO>() {
            @Override
            public AverageTransactionForTransactionTypesResponseDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, BigDecimal> averageTransactions = new TreeMap<>();

                while (rs.next()) {
                    String transactionType = rs.getString("transactionType");
                    averageTransactions.putIfAbsent(transactionType, (rs.getBigDecimal("average")));
                }
                AverageTransactionForTransactionTypesResponseDTO responseDTO = new AverageTransactionForTransactionTypesResponseDTO();
                responseDTO.setAverageTransactions(averageTransactions);
                return responseDTO;
            }
        });
    }

    private String generateAverageTransactionSQLQuery(FilterByDatesRequestDTO requestDTO) {
        int userId = MyUserDetailsService.getCurrentUserId();

        String sql = "SELECT AVG(t.amount) AS average, tt.name AS transactionType\n" +
                "FROM transactions AS t\n" +
                "JOIN transaction_types AS tt ON (t.transaction_type_id = tt.transaction_type_id)\n" +
                "JOIN accounts AS a ON (t.account_id = a.account_id)\n" +
                "WHERE (a.user_id = " + userId + ") AND (t.date_time BETWEEN \"" +
                requestDTO.getStartDate() + "\" AND \"" + requestDTO.getEndDate() + "\")\n" +
                "GROUP BY t.transaction_type_id;";

        return sql;
    }
}
