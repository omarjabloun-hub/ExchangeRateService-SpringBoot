package com.crewmeister.cmcodingchallenge.currency.exception;

public class CurrencyNotFoundException extends RuntimeException     {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
