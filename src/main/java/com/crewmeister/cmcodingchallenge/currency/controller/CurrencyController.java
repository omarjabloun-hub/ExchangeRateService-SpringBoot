package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "Currency API", tags = {"Currency"})
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @ApiOperation(value = "Retrieve all available currencies", notes = "Returns a list of currency codes and descriptions.")
    @GetMapping("/currencies")
    public ResponseEntity<List<CurrencyDto>> getCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }
}
