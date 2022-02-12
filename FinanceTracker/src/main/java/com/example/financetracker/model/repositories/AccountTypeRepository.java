package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {
    
}
