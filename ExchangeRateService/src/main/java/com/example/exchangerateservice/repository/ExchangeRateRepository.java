package com.example.exchangerateservice.repository;

import com.example.exchangerateservice.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {

    ExchangeRate findByCurrencyFromAndCurrencyTo(String currencyFrom, String currencyTo);

    Optional<ExchangeRate> findByCurrencyFromAndCurrencyTo_2(String currencyFrom, String currencyTo);

}
