package com.crewmeister.cmcodingchallenge.exchangerate.controller;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.AggregatedRatesResponse;
import com.crewmeister.cmcodingchallenge.exchangerate.dto.GetExchangeRatesRequest;
import com.crewmeister.cmcodingchallenge.exchangerate.service.ExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Exchange Rate API", tags = {"Exchange Rates"})
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @ApiOperation(value = "Retrieve exchange rates", notes = "Returns exchange rates based on the requested parameters.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of exchange rates"),
            @ApiResponse(code = 400, message = "Invalid input parameters"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/exchange-rates")
    public ResponseEntity<List<AggregatedRatesResponse>> getExchangeRates(
            @Valid GetExchangeRatesRequest request
    ) {
        List<AggregatedRatesResponse> aggregatedRates =
                exchangeRateService.getAggregatedRates(request.getDate(), request.getPage(), request.getSize());
        return ResponseEntity.ok(aggregatedRates);
    }
}
