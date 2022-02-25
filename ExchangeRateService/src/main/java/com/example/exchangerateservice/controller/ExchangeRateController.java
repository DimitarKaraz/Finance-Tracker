package com.example.exchangerateservice.controller;

import com.example.exchangerateservice.service.ExchangeRateService;
import com.example.exchangerateservice.entity.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private Environment environment;

    @GetMapping("/currency_exchange/from/{from}/to/{to}")
    public ExchangeRate getExchangeRate(@PathVariable("from") String from, @PathVariable("to") String to) {
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(from, to);

        String portNumber = environment.getProperty("local.server.port");
        if (portNumber != null) {
            exchangeRate.setPort(Integer.parseInt(portNumber));
        }
        return exchangeRate;
    }
}
