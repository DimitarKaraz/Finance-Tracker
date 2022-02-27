package com.example.financetracker.service;

import com.example.financetracker.model.dao.CurrencyExchangeDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CurrencyExchangeService {

    private Map<String, BigDecimal> exchangeRatesCache = new LinkedHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CurrencyExchangeDAO currencyExchangeDAO;

    public BigDecimal calculateCurrencyConversion(String currencyFrom, String currencyTo, BigDecimal quantity) {
        return quantity.multiply(exchangeRatesCache.get(currencyTo).divide(exchangeRatesCache.get(currencyFrom)));
    }

    @Scheduled(fixedDelay = 10000000000000000L, initialDelay = 5000)
    void updateCacheOnBootUp() {
        System.out.println("Test here");
        this.exchangeRatesCache = currencyExchangeDAO.getExchangeRatesFromDatabase();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Retryable(value = JsonProcessingException.class,
            maxAttempts = 30, backoff = @Backoff(delay = 60*1000))
    void updateDatabase() throws JsonProcessingException {
        currencyExchangeDAO.updateDatabaseExchangeRates(getLatestExchangeRates());
        updateCacheOnBootUp();
    }

    private Map<String, BigDecimal> getLatestExchangeRates() throws JsonProcessingException {
        String json = WebClient.create()
                .get()
                .uri("https://v6.exchangerate-api.com/v6/1b1344854a96238cf8c941ab/latest/BGN")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode node = objectMapper.readTree(json);

        System.out.println("\n******** NODE *************");
        System.out.println(node.get("conversion_rates"));

        Map<String, BigDecimal> allRates = new LinkedHashMap<>();
        if (node.has("conversion_rates")) {
            allRates = objectMapper.convertValue(node.get("conversion_rates"), new TypeReference<>() {});
        }

        for (Map.Entry<String, BigDecimal> entry : allRates.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
        return allRates;
    }




}
