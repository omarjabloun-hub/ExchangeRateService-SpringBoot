package com.crewmeister.cmcodingchallenge.conversion.controller;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;
import com.crewmeister.cmcodingchallenge.conversion.dto.ConvertCurrencyRequest;
import com.crewmeister.cmcodingchallenge.conversion.service.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/conversion")
    public ResponseEntity<ConversionDto> convertCurrency(@RequestBody @Valid ConvertCurrencyRequest request) {
        ConversionDto conversion = conversionService.convert(request.getCurrency(), request.getDate(), request.getAmount());
        return ResponseEntity.ok(conversion);
    }
}
