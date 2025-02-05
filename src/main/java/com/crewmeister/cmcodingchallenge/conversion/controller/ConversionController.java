package com.crewmeister.cmcodingchallenge.conversion.controller;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;
import com.crewmeister.cmcodingchallenge.conversion.service.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api")
public class ConversionController {

    private final ConversionService conversionService;
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/convert")
    public ResponseEntity<ConversionDto> convertCurrency(
            @RequestParam
            @NotBlank(message = "Currency must be provided.")
            @Size(min = 3, max = 3, message = "Currency should be exactly 3 characters.")
            String currency,

            @RequestParam
            @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number.")
            BigDecimal amount,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @PastOrPresent(message = "Date must be in the past or present.")
            LocalDate date
    ) {
        ConversionDto response = conversionService.convert(currency, date, amount);
        return ResponseEntity.ok(response);

    }
}
