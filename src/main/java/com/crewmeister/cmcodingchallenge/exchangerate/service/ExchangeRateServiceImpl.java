package com.crewmeister.cmcodingchallenge.exchangerate.service;

import com.crewmeister.cmcodingchallenge.bundesbank.BundesbankApiClient;
import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.model.Currency;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import com.crewmeister.cmcodingchallenge.exchangerate.dto.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.exchangerate.dto.ExchangeRateTransformer;
import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exchangerate.repository.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

    private final CurrencyRepository currencyRepository;
    private final BundesbankApiClient bundesbankApiClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateTransformer exchangeRateTransformer;


    public ExchangeRateServiceImpl(CurrencyRepository currencyRepository, BundesbankApiClient bundesbankApiClient, ExchangeRateRepository exchangeRateRepository, ExchangeRateTransformer exchangeRateTransformer) {
        this.currencyRepository = currencyRepository;
        this.bundesbankApiClient = bundesbankApiClient;
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateTransformer = exchangeRateTransformer;
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#date != null ? #date.toString() + '_' + #page + '_' + #size : 'all_' + #page + '_' + #size")
    public List<AggregatedRatesResponse> getAggregatedRates(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (date != null) {
            // When a date is provided, get all rates for that date...
            Collection<ExchangeRate> exchangeRates = exchangeRateRepository.findAllExchangeRatesByRateDate(date);
            AggregatedRatesResponse aggregatedResponse = exchangeRateTransformer.transform(new ArrayList<>(exchangeRates));
            Page<AggregatedRatesResponse> pageResponse =
                    new PageImpl<>(Collections.singletonList(aggregatedResponse), pageable, 1);
            return pageResponse.getContent();
        } else {
            Page<LocalDate> distinctDatesPage = exchangeRateRepository.findDistinctRateDates(pageable);

            List<AggregatedRatesResponse> aggregatedResponses = distinctDatesPage.getContent().stream()
                    .map(rateDate -> {
                        Collection<ExchangeRate> rates = exchangeRateRepository.findAllExchangeRatesByRateDate(rateDate);
                        return exchangeRateTransformer.transform(new ArrayList<>(rates));
                    })
                    .collect(Collectors.toList());

            Page<AggregatedRatesResponse> pageResponse =
                    new PageImpl<>(aggregatedResponses, pageable, distinctDatesPage.getTotalElements());
            return pageResponse.getContent();
        }
    }

    /**
     * Update exchange rates for a specific currency by calling the BundesbankApiClient and
     * processing the results to update the DB.
     *
     * @param currencyCode currency code (e.g. "PLN")
     */
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void updateExchangeRateByCurrency(String currencyCode) {
        logger.info("Starting update for currency {} from Bundesbank API", currencyCode);
        try {
            Currency currency = currencyRepository.findById(currencyCode)
                    .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + currencyCode));

            LocalDate lastUpdateDate = currency.getLastUpdated() != null ? currency.getLastUpdated() : LocalDate.MIN;

            // Get the newest exchange rates for the currency after the last update
            List<ExchangeRateDto> exchangeRatesDto = bundesbankApiClient.fetchExchangeRates(currencyCode, lastUpdateDate.toString(), LocalDate.now().toString());

            for (ExchangeRateDto exchangeRateDto : exchangeRatesDto) {
                BigDecimal rateValue = exchangeRateDto.getRate() != null ? new BigDecimal(exchangeRateDto.getRate()) : BigDecimal.ZERO;
                LocalDate rateDate = LocalDate.parse(exchangeRateDto.getDate());
                // Insert/update the exchange rate
                Collection<ExchangeRate> existingRates = exchangeRateRepository
                        .findExchangeRateByCurrency_CodeAndRateDate(currency.getCode(), rateDate);
                if (!existingRates.isEmpty()) {

                    ExchangeRate existing = existingRates.iterator().next();
                    existing.setRate(rateValue);
                    exchangeRateRepository.save(existing);
                    logger.debug("Updated existing rate for {} on {} => {}", currencyCode, rateDate, rateValue);
                } else {
                    ExchangeRate exchangeRate = new ExchangeRate();
                    exchangeRate.setCurrency(currency);
                    exchangeRate.setRateDate(LocalDate.parse(exchangeRateDto.getDate()));
                    exchangeRate.setRate(exchangeRateDto.getRate() != null ? new BigDecimal(exchangeRateDto.getRate()) : BigDecimal.ZERO);
                    exchangeRateRepository.save(exchangeRate);
                    logger.debug("Inserted new rate for {} on {} => {}", currencyCode, rateDate, rateValue);
                }
            }
            // Update currency lastUpdated
            String validFrom = LocalDate.now().toString();
            currency.setLastUpdated(validFrom);
            currencyRepository.save(currency);

            logger.info("Completed update for {} successfully.", currencyCode);

        } catch (Exception ex) {
            logger.error("Error updating Bundesbank data for currency {}: {}", currencyCode, ex.getMessage(), ex);
        }
    }

    /**
     * Updates all currencies in the DB by fetching data from Bundesbank API for each.
     * launched every day at 23:59
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void updateExchangeRates() {
        List<Currency> currencies = currencyRepository.findAll();
        for (Currency currency : currencies) {
            updateExchangeRateByCurrency(currency.getCode());
        }
    }
}


