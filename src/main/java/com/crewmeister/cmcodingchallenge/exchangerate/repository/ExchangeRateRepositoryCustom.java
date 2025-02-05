package com.crewmeister.cmcodingchallenge.exchangerate.repository;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExchangeRateRepositoryCustom {
    Page<AggregatedRatesResponse> findAggregatedExchangeRates(Pageable pageable);
    Page<AggregatedRatesResponse> findAggregatedExchangeRatesByDate(LocalDate date, Pageable pageable);

}
