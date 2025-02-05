package com.crewmeister.cmcodingchallenge.exchangerate.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
