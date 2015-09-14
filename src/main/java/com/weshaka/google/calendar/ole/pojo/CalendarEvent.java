package com.weshaka.google.calendar.ole.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.calendar.model.EventAttendee;

public class CalendarEvent {
    private String summary;
    private String description;
    private String location;
    private LocalDateTime startDateTime;
    private CalendarEventCreator creator;
    private LocalDateTime endDateTime;
    private List<EventAttendee> eventAttendees;
    private String[] recurrence;

    public CalendarEventCreator getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public List<EventAttendee> getEventAttendees() {
        if (eventAttendees == null) {
            this.eventAttendees = new ArrayList<>();
        }
        return this.eventAttendees;
    }

    public String getLocation() {
        return location;
    }

    public String[] getRecurrence() {
        return recurrence;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setCreator(CalendarEventCreator creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setEventAttendees(List<EventAttendee> eventAttendees) {
        this.eventAttendees = eventAttendees;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRecurrence(String[] recurrence) {
        this.recurrence = recurrence;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
