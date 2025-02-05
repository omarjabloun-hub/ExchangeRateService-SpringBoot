package com.crewmeister.cmcodingchallenge.exchangerate.controller;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import com.crewmeister.cmcodingchallenge.exchangerate.service.ExchangeRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
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
            @RequestParam(defaultValue = "0")
            @Min(0) int page,

            @RequestParam(defaultValue = "10")
            @Min(1) @Max(100) int size,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @PastOrPresent(message = "Date must be current or in the past.")
            LocalDate date

    ) {
        List<AggregatedRatesResponse> aggregatedRates = exchangeRateService.getAggregatedRates(date, page, size);
        return ResponseEntity.ok(aggregatedRates);
    }
}
