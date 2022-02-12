package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    List<Account> findAccountsByUser_UserId(int userId);

    Account findAccountsByAccountId(int id);
}
