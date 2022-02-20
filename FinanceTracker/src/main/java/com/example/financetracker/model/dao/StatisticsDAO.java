package com.example.financetracker.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatisticsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

   public void method() {
        int result = jdbcTemplate.queryForObject(
               "SELECT COUNT(*) FROM USERS WHERE gender = 'male'", Integer.class);
       System.out.println(result);
    }


}
