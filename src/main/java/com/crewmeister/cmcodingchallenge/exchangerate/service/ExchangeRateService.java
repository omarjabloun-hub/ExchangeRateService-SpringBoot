package com.crewmeister.cmcodingchallenge.exchangerate.service;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {
    List<AggregatedRatesResponse> getAggregatedRates(LocalDate date, int page, int size);

    void updateExchangeRates();
}
