package com.crewmeister.cmcodingchallenge.exchangerate.repository;

import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long>, ExchangeRateRepositoryCustom {
    Optional<ExchangeRate> findAllExchangeRatesByCurrency_Code(String currencyCode);
    Collection<ExchangeRate> findAllExchangeRatesByRateDate(java.time.LocalDate rateDate);
    Collection<ExchangeRate> findExchangeRateByCurrency_CodeAndRateDate(String currencyCode, java.time.LocalDate rateDate);
}
