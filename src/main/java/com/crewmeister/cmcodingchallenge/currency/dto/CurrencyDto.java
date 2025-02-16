package com.crewmeister.cmcodingchallenge.currency.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "DTO representing currency details")
public class CurrencyDto {

    @ApiModelProperty(value = "ISO 4217 currency code", example = "USD")
    private String code;

    @ApiModelProperty(value = "Currency description", example = "United States Dollar")
    private String description;

    public CurrencyDto(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
