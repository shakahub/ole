/**
 * 
 */
package com.weshaka.ole.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.weshaka.google.calendar.ole.CalendarServiceFactory;
import com.weshaka.google.calendar.ole.pojo.CalendarEvent;
/**
 * @author ema
 *
 */
@RestController
public class BeaconController {
    @RequestMapping("/calendar-events")
    public @ResponseBody List<CalendarEvent> hello() throws IOException{
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
                CalendarServiceFactory.getCalendarService();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            items.forEach(event -> {
                CalendarEvent e = new CalendarEvent();
                DateTime start = event.getStart().getDateTime(); 
                if (start == null) {
                    start = event.getStart().getDate();
                    e.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(),ZoneId.systemDefault()));
                }
                e.setSummary(event.getSummary());
                calendarEvents.add(e);
                
            });
        }
        return calendarEvents;
    }
}
