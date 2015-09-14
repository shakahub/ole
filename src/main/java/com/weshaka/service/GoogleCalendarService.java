/**
 *
 */
package com.weshaka.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;
import com.weshaka.google.calendar.ole.CalendarServiceFactory;

/**
 * @author ema
 */
@Service
public class GoogleCalendarService implements CalendarService{
    @Async
    public Future<Events> getPrimaryEvents(String orderBy, Integer limit) throws IOException, GeneralSecurityException {
        final com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        // List the next ${limit} events from the primary calendar, order by ${orderBy}.
        final DateTime now = new DateTime(System.currentTimeMillis());
        final Events events = service.events().list("primary").setMaxResults(limit).setTimeMin(now).setOrderBy(orderBy).setSingleEvents(true).execute();
        return new AsyncResult<Events>(events);
    }
}
