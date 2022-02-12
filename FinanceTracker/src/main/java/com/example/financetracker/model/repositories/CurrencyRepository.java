package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {


}
