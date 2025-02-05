package com.crewmeister.cmcodingchallenge.exchangerate.repository;

import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findAllExchangeRatesByCurrency_Code(String currencyCode);
    Collection<ExchangeRate> findAllExchangeRatesByRateDate(java.time.LocalDate rateDate);
    Collection<ExchangeRate> findExchangeRateByCurrency_CodeAndRateDate(String currencyCode, java.time.LocalDate rateDate);
    @Query("SELECT DISTINCT er.rateDate FROM ExchangeRate er ORDER BY er.rateDate DESC")
    Page<LocalDate> findDistinctRateDates(Pageable pageable);
}
