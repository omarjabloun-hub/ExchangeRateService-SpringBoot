package com.crewmeister.cmcodingchallenge.conversion.service;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;
import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exchangerate.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.exchangerate.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;

@Service
public class ConversionServiceImpl implements ConversionService {
    private final ExchangeRateRepository exchangeRateRepository;

    public ConversionServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ConversionDto convert(String currencyCode, LocalDate date, BigDecimal amount) {

        Collection<ExchangeRate> rates = exchangeRateRepository
                .findExchangeRateByCurrency_CodeAndRateDate(currencyCode, date);

        if (rates.isEmpty()) {
            throw new ExchangeRateNotFoundException("Exchange rate not found");
        }
        ExchangeRate rate = rates.iterator().next();

        BigDecimal exchangeRateValue = rate.getRate();
        BigDecimal converted = (exchangeRateValue.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO
                : amount.divide(exchangeRateValue, 2, RoundingMode.HALF_UP);
        return new ConversionDto(currencyCode, date, amount, exchangeRateValue, converted);

    }
}
