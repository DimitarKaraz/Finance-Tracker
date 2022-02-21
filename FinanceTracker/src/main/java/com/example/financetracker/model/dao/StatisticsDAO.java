package com.example.financetracker.model.dao;

import com.example.financetracker.model.dto.budgetDTOs.BudgetByFiltersDTO;
import com.example.financetracker.model.pojo.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class StatisticsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Budget> getBudgetsByFilters(BudgetByFiltersDTO filtersDTO) {
        String sql = "SELECT * " +
                "FROM budgets AS b " +
                "";


        RowMapper<Budget> budgetRowMapper = new RowMapper<Budget>() {
            @Override
            public Budget mapRow(ResultSet rs, int rowNum) throws SQLException {
                return null;
            }
        }



        jdbcTemplate.query(sql, budgetRowMapper);






    }


}
