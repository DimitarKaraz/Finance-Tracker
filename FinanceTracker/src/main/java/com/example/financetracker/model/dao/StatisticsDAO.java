package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class StatisticsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ModelMapper modelMapper;

    public String getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        String sql = "(b.start_date BETWEEN "+filtersDTO.getStartDate()+" AND "+filtersDTO.getEndDate()+"\n";

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
/*

        BudgetResponseDTO budgetRowMapper = new RowMapper<BudgetResponseDTO>() {
            @Override
            public BudgetResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                BudgetResponseDTO budgetResponseDTO = new BudgetResponseDTO();
                budgetResponseDTO.setBudgetId(rs.getInt("budget_id"));
                budgetResponseDTO.setName(rs.getString("name"));
                budgetResponseDTO.setAmountSpent(rs.getBigDecimal("amount_spent"));
                budgetResponseDTO.setMaxLimit(rs.getBigDecimal("max_limit"));
                budgetResponseDTO.setInterval(modelMapper.map(r));


            }
        }



        jdbcTemplate.query(sql, budgetRowMapper);



*/

    return sql;

    }


}
