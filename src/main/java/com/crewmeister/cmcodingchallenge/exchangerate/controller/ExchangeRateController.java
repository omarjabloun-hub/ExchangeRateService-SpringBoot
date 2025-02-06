package com.crewmeister.cmcodingchallenge.exchangerate.controller;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import com.crewmeister.cmcodingchallenge.exchangerate.dto.GetExchangeRatesRequest;
import com.crewmeister.cmcodingchallenge.exchangerate.service.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<List<AggregatedRatesResponse>> getExchangeRates(
            @Valid GetExchangeRatesRequest request
    ) {
        List<AggregatedRatesResponse> aggregatedRates =
                exchangeRateService.getAggregatedRates(request.getDate(), request.getPage(), request.getSize());
        return ResponseEntity.ok(aggregatedRates);
    }
}
