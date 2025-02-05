package com.crewmeister.cmcodingchallenge.common;

import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.exchangerate.exception.ExchangeRateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({TimeFormatException.class})
    public ResponseEntity<Object> handleTimeFormatException(TimeFormatException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
    @ExceptionHandler({CurrencyNotFoundException.class})
    public ResponseEntity<Object> handleCurrencyNotFoundException(CurrencyNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
    @ExceptionHandler({ExchangeRateNotFoundException.class})
    public ResponseEntity<Object> handleExchangeRateNotFoundException(ExchangeRateNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
