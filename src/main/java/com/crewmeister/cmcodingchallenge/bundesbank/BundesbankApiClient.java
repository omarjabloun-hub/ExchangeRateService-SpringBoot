package com.crewmeister.cmcodingchallenge.bundesbank;

import com.crewmeister.cmcodingchallenge.exchangerate.dto.ExchangeRateDto;

import java.util.*;

public interface BundesbankApiClient {

    List<ExchangeRateDto> fetchExchangeRates(String currencyCode, String startPeriod, String endPeriod );
}
