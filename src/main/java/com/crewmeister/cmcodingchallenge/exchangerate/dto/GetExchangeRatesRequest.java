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
    @Min(value = 0, message = "page.number.min")
    private int page = 0;

    @ApiModelProperty(value = "Number of items per page", example = "10")
    @Min(value = 1, message = "page.size.min")
    @Max(value = 100, message = "page.size.max")
    private int size = 10;

    @ApiModelProperty(value = "Date for retrieving exchange rates", example = "2024-02-14")
    @PastOrPresent(message = "date.must.be.past.or.present")
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
