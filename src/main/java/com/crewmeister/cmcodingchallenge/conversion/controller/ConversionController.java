package com.crewmeister.cmcodingchallenge.conversion.controller;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;
import com.crewmeister.cmcodingchallenge.conversion.dto.ConvertCurrencyRequest;
import com.crewmeister.cmcodingchallenge.conversion.service.ConversionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api")
@Api(value = "Currency Conversion API", tags = {"Conversion"})
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @ApiOperation(value = "Convert currency to EUR", notes = "Converts an amount from a given currency to EUR using historical exchange rates.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful conversion"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/conversion")
    public ResponseEntity<ConversionDto> convertCurrency(@RequestBody @Valid ConvertCurrencyRequest request) {
        ConversionDto conversion = conversionService.convert(request.getCurrency(), request.getDate(), request.getAmount());
        return ResponseEntity.ok(conversion);
    }
}