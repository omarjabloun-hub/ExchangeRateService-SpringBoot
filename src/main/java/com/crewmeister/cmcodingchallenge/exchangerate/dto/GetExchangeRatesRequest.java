package com.crewmeister.cmcodingchallenge.exchangerate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@ApiModel(description = "Request DTO for retrieving exchange rates")
public class GetExchangeRatesRequest {
    @ApiModelProperty(value = "Page number for pagination", example = "0")
    @Min(0)
    private int page = 0;

    @ApiModelProperty(value = "Number of items per page", example = "10")
    @Min(1)
    @Max(100)
    private int size = 10;

    @ApiModelProperty(value = "Date for retrieving exchange rates", example = "2024-02-14")
    @PastOrPresent(message = "Date must be current or in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
