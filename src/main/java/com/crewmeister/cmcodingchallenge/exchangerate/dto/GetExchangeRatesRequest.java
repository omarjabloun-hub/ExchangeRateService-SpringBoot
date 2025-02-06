package com.crewmeister.cmcodingchallenge.exchangerate.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;


public class GetExchangeRatesRequest {
    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 10;

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
