package com.crewmeister.cmcodingchallenge.conversion.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@ApiModel(description = "Request DTO for currency conversion")
public class ConvertCurrencyRequest {
    @ApiModelProperty(value = "Currency code (ISO 4217)", example = "USD", required = true)
    @NotBlank(message = "Currency must be provided.")
    @Size(min = 3, max = 3, message = "Currency should be exactly 3 characters.")
    private String currency;

    @ApiModelProperty(value = "Amount to convert", example = "100.00", required = true)
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number.")
    private BigDecimal amount;

    @ApiModelProperty(value = "Date for exchange rate", example = "2024-02-14", required = true)
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