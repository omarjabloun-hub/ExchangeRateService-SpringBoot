package com.crewmeister.cmcodingchallenge.exchangerate.repository;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AggregatedRatesResponse> findAggregatedExchangeRates(Pageable pageable) {
        // Step 1: Retrieve distinct rate dates with pagination and sorting (sorted by rateDate descending)
        String distinctDatesJpql = "SELECT DISTINCT er.rateDate FROM ExchangeRate er ORDER BY er.rateDate DESC";
        TypedQuery<LocalDate> distinctDatesQuery = entityManager.createQuery(distinctDatesJpql, LocalDate.class);
        distinctDatesQuery.setFirstResult((int) pageable.getOffset());
        distinctDatesQuery.setMaxResults(pageable.getPageSize());
        List<LocalDate> distinctDates = distinctDatesQuery.getResultList();

        // Count total distinct dates for pagination metadata
        String countJpql = "SELECT COUNT(DISTINCT er.rateDate) FROM ExchangeRate er";
        Long totalDistinctDates = (Long) entityManager.createQuery(countJpql).getSingleResult();

        if (distinctDates.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalDistinctDates);
        }

        // Step 2: Retrieve all exchange rates for the selected dates in one query
        String ratesJpql = "SELECT er FROM ExchangeRate er WHERE er.rateDate IN :dates ORDER BY er.rateDate DESC";
        TypedQuery<ExchangeRate> ratesQuery = entityManager.createQuery(ratesJpql, ExchangeRate.class);
        ratesQuery.setParameter("dates", distinctDates);
        List<ExchangeRate> exchangeRates = ratesQuery.getResultList();

        // Group exchange rates by date. The grouping is performed in Java but over a limited subset.
        Map<LocalDate, List<ExchangeRate>> groupedRates = exchangeRates.stream()
                .collect(Collectors.groupingBy(ExchangeRate::getRateDate));

        // Build the list of AggregatedRatesResponse maintaining the same order as distinctDates
        List<AggregatedRatesResponse> responses = distinctDates.stream()
                .map(date -> {
                    // Convert the list of ExchangeRate for this date into a Map of currencyCode -> rate.
                    Map<String, BigDecimal> ratesMap = groupedRates.getOrDefault(date, Collections.emptyList())
                            .stream()
                            .collect(Collectors.toMap(ExchangeRate::getCurrencyCode, ExchangeRate::getRate));
                    return new AggregatedRatesResponse(date, ratesMap);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, totalDistinctDates);
    }

    @Override
    public Page<AggregatedRatesResponse> findAggregatedExchangeRatesByDate(LocalDate date, Pageable pageable) {
        // Step 1: Check for the given date (should yield one result at most)
        String distinctDateJpql = "SELECT DISTINCT er.rateDate FROM ExchangeRate er WHERE er.rateDate = :date";
        TypedQuery<LocalDate> dateQuery = entityManager.createQuery(distinctDateJpql, LocalDate.class);
        dateQuery.setParameter("date", date);
        List<LocalDate> dates = dateQuery.getResultList();

        // For pagination metadata, total is either 0 or 1.
        long total = dates.isEmpty() ? 0L : 1L;
        if (dates.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        // Step 2: Retrieve all exchange rate rows for that specific date
        String ratesJpql = "SELECT er FROM ExchangeRate er WHERE er.rateDate = :date ORDER BY er.rateDate DESC";
        TypedQuery<ExchangeRate> ratesQuery = entityManager.createQuery(ratesJpql, ExchangeRate.class);
        ratesQuery.setParameter("date", date);
        List<ExchangeRate> exchangeRates = ratesQuery.getResultList();

        // Since all rows have the same date, build the map directly.
        Map<String, BigDecimal> ratesMap = exchangeRates.stream()
                .collect(Collectors.toMap(er -> er.getCurrency().getCode(), ExchangeRate::getRate));

        AggregatedRatesResponse response = new AggregatedRatesResponse(date, ratesMap);
        // Even though there is only one group, we wrap it in a PageImpl.
        List<AggregatedRatesResponse> responses = Collections.singletonList(response);
        return new PageImpl<>(responses, pageable, total);
    }
}
