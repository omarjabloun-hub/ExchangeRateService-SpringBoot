package com.crewmeister.cmcodingchallenge.exchangerate.dto;

import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExchangeRateTransformer {

    public AggregatedRatesResponse transform(List<ExchangeRate> exchangeRates) {
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            return new AggregatedRatesResponse(null, Collections.emptyMap());
        }

        // Same Rate Date for all exchange rates
        LocalDate date = exchangeRates.get(0).getRateDate();
        Map<String, BigDecimal> rates = exchangeRates.stream()
                .collect(Collectors.toMap(
                        er -> er.getCurrency().getCode(),
                        ExchangeRate::getRate
                ));
        return new AggregatedRatesResponse(date, rates);
    }
}
