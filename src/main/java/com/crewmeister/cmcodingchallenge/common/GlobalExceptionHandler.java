package com.crewmeister.cmcodingchallenge.common;

import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.exchangerate.exception.ExchangeRateNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final Locale locale;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = LocaleContextHolder.getLocale();
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
                errors.put(error.getField(), messageSource.getMessage("bind.exception", new Object[]{error.getRejectedValue()}, locale))
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({TimeFormatException.class})
    public ResponseEntity<Object> handleTimeFormatException(TimeFormatException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageSource.getMessage("time.format.exception", null, locale));
    }

    @ExceptionHandler({CurrencyNotFoundException.class})
    public ResponseEntity<Object> handleCurrencyNotFoundException(CurrencyNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageSource.getMessage("currency.not.found", null, locale));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageSource.getMessage("runtime.exception", null, locale));
    }

    @ExceptionHandler({ExchangeRateNotFoundException.class})
    public ResponseEntity<Object> handleExchangeRateNotFoundException(ExchangeRateNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageSource.getMessage("exchange.rate.not.found", null, locale));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), messageSource.getMessage(error.getDefaultMessage(), null, locale))
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, String>> handleDateParseException(DateTimeParseException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("invalid.date.format", null, locale));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
