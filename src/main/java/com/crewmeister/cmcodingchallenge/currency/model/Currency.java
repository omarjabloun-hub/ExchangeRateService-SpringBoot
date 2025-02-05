package com.crewmeister.cmcodingchallenge.currency.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "currency")
public class Currency {
    public Currency() {}

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated.toLocalDate();
    }

    public Instant getLastUpdatedInstant() {
        return lastUpdated.toInstant(ZoneOffset.UTC);

    }

    public void setLastUpdated(String lastUpdated) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Try parsing with datetime format
            this.lastUpdated = LocalDateTime.parse(lastUpdated, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            // If it fails, try parsing with date format and set time to midnight
            this.lastUpdated = LocalDate.parse(lastUpdated, dateFormatter).atStartOfDay();
        }
    }
    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
