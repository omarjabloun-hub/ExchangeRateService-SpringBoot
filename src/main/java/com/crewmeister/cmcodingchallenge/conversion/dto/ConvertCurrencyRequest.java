package com.crewmeister.cmcodingchallenge.conversion.dto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public class ConvertCurrencyRequest {
    @NotBlank(message = "Currency must be provided.")
    @Size(min = 3, max = 3, message = "Currency should be exactly 3 characters.")
    private String currency;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number.")
    private BigDecimal amount;

    @PastOrPresent(message = "Date must be in the past or present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
