package com.example.financetracker.model.dao;

import com.example.financetracker.model.pojo.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

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
