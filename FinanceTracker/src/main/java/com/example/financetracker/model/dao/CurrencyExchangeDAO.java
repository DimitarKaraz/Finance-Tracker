package com.example.financetracker.model.dao;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyExchangeDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, BigDecimal> getExchangeRatesFromDatabase(String currencyFrom, String currencyTo) {
        final String sql = "SELECT abbreviation, exchange_rate_from_BGN\n" +
                "FROM currencies;";

        return jdbcTemplate.query(sql,new ResultSetExtractor<Map<String, BigDecimal>>() {
            @Override
            public Map<String, BigDecimal> extractData(@NotNull ResultSet rs) throws SQLException, DataAccessException {
                Map<String, BigDecimal> rates = new HashMap<>();

                while (rs.next()) {
                   rates.put(rs.getString("abbreviation"), rs.getBigDecimal("exchange_rate_from_BGN"));
                }
                return rates;
            }
        });
    }

    public void updateDatabase(Map<String, BigDecimal> rates) {
        if (rates == null || rates.isEmpty()) {
            return;
        }
        final String sqlUpdate = "UPDATE currencies\n" +
                                "SET exchange_rate_from_BGN = ?\n" +
                                "WHERE currency = \"?\";";
        int rowsAffected = jdbcTemplate.batchUpdate(sqlUpdate, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, );
            }

            @Override
            public int getBatchSize() {
                return 0;
            }
        })
    }

}
