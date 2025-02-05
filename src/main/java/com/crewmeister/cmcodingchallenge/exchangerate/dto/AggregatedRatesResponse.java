package com.crewmeister.cmcodingchallenge.exchangerate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class AggregatedRatesResponse {
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public AggregatedRatesResponse(LocalDate date, Map<String, BigDecimal> rates) {
        this.date = date;
        this.rates = rates;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
