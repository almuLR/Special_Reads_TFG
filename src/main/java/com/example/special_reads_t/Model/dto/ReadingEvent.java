package com.example.special_reads_t.Model.dto;

import java.beans.Transient;
import java.time.LocalDateTime;

public class ReadingEvent {
    private String title;
    private LocalDateTime date;
    private String status; // "Leyendo" o "Leído"

    public ReadingEvent(String title, LocalDateTime date, String status) {
        this.title = title;
        this.date = date;
        this.status = status;
    }
    // getters
    public String getTitle() { return title; }
    public LocalDateTime getDate() { return date; }
    public String getStatus() { return status; }

    @Transient
    public boolean isReading() {
        return "Leyendo".equalsIgnoreCase(this.status);
    }
    @Transient
    public boolean isFinished() {
        return "Leído".equalsIgnoreCase(this.status);
    }

}