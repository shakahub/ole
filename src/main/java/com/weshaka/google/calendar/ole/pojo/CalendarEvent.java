package com.weshaka.google.calendar.ole.pojo;

import java.time.LocalDateTime;

public class CalendarEvent {
    private String summary;
    private LocalDateTime startDateTime;
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
