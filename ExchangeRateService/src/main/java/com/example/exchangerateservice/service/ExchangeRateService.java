package com.example.exchangerateservice.service;

import com.example.exchangerateservice.entity.ExchangeRate;
import com.example.exchangerateservice.exception.NotFoundException;
import com.example.exchangerateservice.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;


    public ExchangeRate getExchangeRate(String from, String to) {
        return exchangeRateRepository.findByCurrencyFromAndCurrencyTo(from, to);
//                .orElseThrow(() -> {throw new NotFoundException(from + " to " + to + " exchange rate not found.");});
    }
}
