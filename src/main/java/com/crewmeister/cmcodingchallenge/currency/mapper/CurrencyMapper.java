package com.crewmeister.cmcodingchallenge.currency.mapper;

import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.currency.model.Currency;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CurrencyMapper {

    public CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(currency.getCode(), currency.getDescription());
    }

    public List<CurrencyDto> toDtoList(List<Currency> currencies) {
        return currencies.stream().map(this::toDto).collect(Collectors.toList());
    }
}
