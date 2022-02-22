package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.specialStatisticsDTOs.CashFlowsResponseDTO;
import com.example.financetracker.model.dto.specialStatisticsDTOs.FilterByDatesRequestDTO;
import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class SpecialStatisticsDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CashFlowsResponseDTO getCashFlowsForAccounts(FilterByDatesRequestDTO requestDTO) {
        final String sql = generateCashFlowSQLQuery(requestDTO);

        return jdbcTemplate.query(sql, new ResultSetExtractor<CashFlowsResponseDTO>() {
            @Override
            public CashFlowsResponseDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, Map<String, Integer>> accountCashFlows = new LinkedHashMap<>();

                while (rs.next()) {
                    String accountName = rs.getString("accountName");
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
                "a.name AS accountName\n" +
                "FROM transactions AS t\n" +
                "JOIN transaction_types AS tt ON (t.transaction_type_id = tt.transaction_type_id)\n" +
                "JOIN accounts AS a ON (t.account_id = a.account_id)\n" +
                "WHERE (a.user_id = " + userId + ") AND (t.start_date BETWEEN \"" +
                requestDTO.getStartDate() + "\" AND \"" + requestDTO.getEndDate() + "\")\n" +
                "GROUP BY t.account_id\n" +
                "ORDER BY a.name ASC;";

        return sql;
    }
}
