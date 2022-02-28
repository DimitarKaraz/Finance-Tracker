package com.example.financetracker.service;

import com.example.financetracker.model.dao.CurrencyExchangeDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CurrencyExchangeService {

    private Map<String, BigDecimal> exchangeRatesCache = new LinkedHashMap<>();

    @Value("${exchangerate-api.uri}")
    private String exchangeRateAPIuri;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CurrencyExchangeDAO currencyExchangeDAO;

    public BigDecimal calculateCurrencyConversion(String currencyFrom, String currencyTo, BigDecimal quantity) {
        return quantity.multiply(exchangeRatesCache.get(currencyTo).divide(exchangeRatesCache.get(currencyFrom), 2, RoundingMode.HALF_EVEN));
    }

    @PostConstruct
    void updateCacheOnBootUp() {
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
                .uri(exchangeRateAPIuri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode node = objectMapper.readTree(json);
        Map<String, BigDecimal> allRates = new LinkedHashMap<>();
        if (node.has("conversion_rates")) {
            allRates = objectMapper.convertValue(node.get("conversion_rates"), new TypeReference<>() {});
        }
        return allRates;
    }




}
