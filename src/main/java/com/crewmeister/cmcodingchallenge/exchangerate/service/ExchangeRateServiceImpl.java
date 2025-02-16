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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
    private final CurrencyRepository currencyRepository;
    private final BundesbankApiClient bundesbankApiClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateTransformer exchangeRateTransformer;
    private final MessageSource messageSource;
    private final Locale locale;

    public ExchangeRateServiceImpl(CurrencyRepository currencyRepository, BundesbankApiClient bundesbankApiClient,
                                   ExchangeRateRepository exchangeRateRepository,
                                   ExchangeRateTransformer exchangeRateTransformer,
                                   MessageSource messageSource) {
        this.currencyRepository = currencyRepository;
        this.bundesbankApiClient = bundesbankApiClient;
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateTransformer = exchangeRateTransformer;
        this.messageSource = messageSource;
        this.locale = LocaleContextHolder.getLocale();
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#date != null ? #date.toString() + '_' + #page + '_' + #size : 'all_' + #page + '_' + #size")
    public List<AggregatedRatesResponse> getAggregatedRates(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (date != null) {
            Collection<ExchangeRate> exchangeRates = exchangeRateRepository.findAllExchangeRatesByRateDate(date);
            AggregatedRatesResponse aggregatedResponse = exchangeRateTransformer.transform(new ArrayList<>(exchangeRates));
            return Collections.singletonList(aggregatedResponse);
        } else {
            Page<LocalDate> distinctDatesPage = exchangeRateRepository.findDistinctRateDates(pageable);
            List<AggregatedRatesResponse> aggregatedResponses = distinctDatesPage.getContent().stream()
                    .map(rateDate -> {
                        Collection<ExchangeRate> rates = exchangeRateRepository.findAllExchangeRatesByRateDate(rateDate);
                        return exchangeRateTransformer.transform(new ArrayList<>(rates));
                    })
                    .collect(Collectors.toList());
            return aggregatedResponses;
        }
    }

    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void updateExchangeRateByCurrency(String currencyCode) {
        logger.info("Starting update for currency {} from Bundesbank API", currencyCode);
        try {
            Currency currency = currencyRepository.findById(currencyCode)
                    .orElseThrow(() -> new CurrencyNotFoundException(
                            messageSource.getMessage("currency.not.found", null , locale)));
            LocalDate lastUpdateDate = currency.getLastUpdated() != null ? currency.getLastUpdated() : LocalDate.MIN;
            List<ExchangeRateDto> exchangeRatesDto = bundesbankApiClient.fetchExchangeRates(currencyCode, lastUpdateDate.toString(), LocalDate.now().toString());
            for (ExchangeRateDto exchangeRateDto : exchangeRatesDto) {
                BigDecimal rateValue = exchangeRateDto.getRate() != null ? new BigDecimal(exchangeRateDto.getRate()) : BigDecimal.ZERO;
                LocalDate rateDate = LocalDate.parse(exchangeRateDto.getDate());
                Collection<ExchangeRate> existingRates = exchangeRateRepository.findExchangeRateByCurrency_CodeAndRateDate(currency.getCode(), rateDate);
                if (!existingRates.isEmpty()) {
                    ExchangeRate existing = existingRates.iterator().next();
                    existing.setRate(rateValue);
                    exchangeRateRepository.save(existing);
                } else {
                    ExchangeRate exchangeRate = new ExchangeRate(currency, rateDate, rateValue);
                    exchangeRateRepository.save(exchangeRate);
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

    @Scheduled(cron = "0 59 23 * * ?")
    public void updateExchangeRates() {
        currencyRepository.findAll().forEach(currency -> updateExchangeRateByCurrency(currency.getCode()));
    }
}
