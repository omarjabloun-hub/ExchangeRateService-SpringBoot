package com.crewmeister.cmcodingchallenge.conversion.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

@ApiModel(description = "DTO for currency conversion response")
public class ConversionDto {
    @ApiModelProperty(value = "Currency code", example = "USD")
    private final String currency;

    @ApiModelProperty(value = "Exchange rate date", example = "2024-02-14")
    private final LocalDate date;

    @ApiModelProperty(value = "Original amount before conversion", example = "100.00")
    private final BigDecimal originalAmount;

    @ApiModelProperty(value = "Exchange rate used for conversion", example = "1.12")
    private final BigDecimal exchangeRate;

    @ApiModelProperty(value = "Converted amount in EUR", example = "89.29")
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