package com.crewmeister.cmcodingchallenge.conversion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConversionDto {
    private final String currency;
    private final LocalDate date;
    private final BigDecimal originalAmount;
    private final BigDecimal exchangeRate;
    private final BigDecimal convertedAmount;

    public ConversionDto(String currency, LocalDate date, BigDecimal originalAmount, BigDecimal exchangeRate, BigDecimal convertedAmount) {
        this.currency = currency;
        this.date = date;
        this.originalAmount = originalAmount;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCurrency() {
        return currency;
    }
}