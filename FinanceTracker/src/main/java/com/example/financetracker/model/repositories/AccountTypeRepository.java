package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {
    
}
