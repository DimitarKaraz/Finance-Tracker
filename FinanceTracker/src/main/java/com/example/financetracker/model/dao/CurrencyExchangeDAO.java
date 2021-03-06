package com.example.financetracker.model.dao;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyExchangeDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Map<String, BigDecimal> getExchangeRatesFromDatabase() {
        final String sql = "SELECT c.abbreviation, er.exchange_rate_from_BGN\n" +
                "FROM currencies AS c\n" +
                "JOIN exchange_rates AS er ON (c.currency_id = er.currency_id);";

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

    @Transactional
    public int[] updateDatabaseExchangeRates(Map<String, BigDecimal> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<String, BigDecimal>> ratesList = new ArrayList<>(rates.entrySet());

        final String sqlUpdate = "UPDATE exchange_rates AS er\n" +
                                "JOIN currencies AS c USING (currency_id)\n" +
                                "SET er.exchange_rate_from_BGN = ?\n" +
                                "WHERE c.abbreviation = ? ;";
        return jdbcTemplate.batchUpdate(sqlUpdate, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, ratesList.get(i).getValue());
                ps.setString(2, ratesList.get(i).getKey());
            }

            @Override
            public int getBatchSize() {
                return ratesList.size();
            }
        });
    }



}
