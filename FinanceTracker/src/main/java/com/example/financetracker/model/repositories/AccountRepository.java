package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Page<Account> findAccountsByUser_UserId(int userId, Pageable pageable);

    boolean existsAccountByUser_UserIdAndName(int userId, String name);



}
