package com.crewmeister.cmcodingchallenge.exchangerate.dto;


public class ExchangeRateDto {
    private final String currencyCode;
    private final String date;
    private final String rate;

    public ExchangeRateDto(String currencyCode, String date, String rate) {
        this.currencyCode = currencyCode;
        this.date = date;
        this.rate = rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getDate() {
        return date;
    }

    public String getRate() {
        return rate;
    }
}
