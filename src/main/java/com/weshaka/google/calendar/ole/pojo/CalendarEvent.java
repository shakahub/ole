package com.weshaka.google.calendar.ole.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.calendar.model.EventAttendee;

public class CalendarEvent {
    private String summary;
    private LocalDateTime startDateTime;
    private List<EventAttendee> eventAttendees;
    
    public List<EventAttendee> getEventAttendees() {
        if(eventAttendees==null){
            this.eventAttendees = new ArrayList<>();
        }
        return this.eventAttendees;
    }
    public void setEventAttendees(List<EventAttendee> eventAttendees) {
        this.eventAttendees = eventAttendees;
    }
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
