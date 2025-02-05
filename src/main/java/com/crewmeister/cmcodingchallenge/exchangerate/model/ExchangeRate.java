package com.crewmeister.cmcodingchallenge.exchangerate.model;

import com.crewmeister.cmcodingchallenge.currency.model.Currency;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {

    public ExchangeRate() {}

    public ExchangeRate(Currency currency, LocalDate rateDate, BigDecimal rate) {
        this.currency = currency;
        this.rateDate = rateDate;
        this.rate = rate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_rate_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "rate_value")
    private BigDecimal rate;

    // getter
    public Currency getCurrency() {
        return currency;
    }
    public LocalDate getRateDate() {
        return rateDate;
    }
    public BigDecimal getRate() {
        return rate;
    }
    public String getCurrencyCode() {
        return currency.getCode();
    }
    // setter
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public void setRateDate(LocalDate rateDate) {
        this.rateDate = rateDate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}
