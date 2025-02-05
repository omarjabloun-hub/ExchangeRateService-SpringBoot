package com.crewmeister.cmcodingchallenge.conversion.service;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ConversionService {
    ConversionDto convert(String currencyCode, LocalDate date, BigDecimal amount);
}
