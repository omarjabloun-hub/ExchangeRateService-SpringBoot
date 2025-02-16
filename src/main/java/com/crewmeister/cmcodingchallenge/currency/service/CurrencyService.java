package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.currency.mapper.CurrencyMapper;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
    }

    public List<CurrencyDto> getAllCurrencies() {
        return currencyMapper.toDtoList(currencyRepository.findAll());
    }
}
