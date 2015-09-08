/**
 * 
 */
package com.weshaka.ole.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.weshaka.google.calendar.ole.CalendarServiceFactory;
import com.weshaka.google.calendar.ole.pojo.CalendarEvent;
import com.weshaka.ole.entity.BeaconSubject;
import com.weshaka.ole.exceptions.BeaconBusinessIDNotFoundException;
import com.weshaka.ole.exceptions.BeaconMacIdNotValidException;
import com.weshaka.ole.exceptions.BeaconNotFoundException;
import com.weshaka.ole.repository.BeaconSubjectRepository;
import com.weshaka.ole.repository.BeaconSubjectRepositoryCustom;

/**
 * @author ema
 */
@RestController
public class BeaconController extends CommonController {

    @Autowired
    private BeaconSubjectRepository repository;

    @Autowired
    private BeaconSubjectRepositoryCustom repositoryCustom;

    @RequestMapping("/calendar-events")
    public @ResponseBody List<CalendarEvent> getCalendarEventsAPI() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        // com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute();
        List<Event> items = events.getItems();
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (items.size() == 0) {
            debug.print("No upcoming events found for {}", "default primary");
        } else {
            debug.print("Upcoming events {}", "default primary");
            items.forEach(event -> {
                CalendarEvent e = new CalendarEvent();
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                debug.print("eventStart={}" + start);
                e.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(), ZoneId.systemDefault()));
                List<EventAttendee> attendees = event.getAttendees();
                if (attendees != null)
                    e.getEventAttendees().addAll(attendees);
                e.setSummary(event.getSummary());
                calendarEvents.add(e);

            });
        }
        return calendarEvents;
    }

    @RequestMapping("/calendar-events/{calendarId:.+}")
    public @ResponseBody List<CalendarEvent> getCalendarEventsByCalendarIdAPI(@PathVariable("calendarId") String calendarId) throws IOException,
            GeneralSecurityException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        // com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        debug.print("CalendarId={}", calendarId);
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(calendarId).setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute();
        List<Event> items = events.getItems();
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (items.size() == 0) {
            debug.print("No upcoming events found for {}", calendarId);
        } else {
            debug.print("Upcoming events for {}", calendarId);
            items.forEach(event -> {
                CalendarEvent e = new CalendarEvent();
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                e.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(), ZoneId.systemDefault()));
                List<EventAttendee> attendees = event.getAttendees();
                if (attendees != null)
                    e.getEventAttendees().addAll(attendees);
                e.setSummary(event.getSummary());
                calendarEvents.add(e);

            });
        }
        return calendarEvents;
    }

    @RequestMapping("/calendar-events/{calendarId:.+}/free-busy")
    public @ResponseBody FreeBusyCalendar getCalendarEventsFreeBusyByCalendarIdAPI(@PathVariable("calendarId") String calendarId) throws IOException,
            GeneralSecurityException {
        return getCalendarEventsFreeBusyByCalendarId(calendarId);
    }

    @RequestMapping("/beacons/{beaconMacId}")
    public @ResponseBody BeaconSubject getBeaconSubjectByBeaconMacIdAPI(@PathVariable("beaconMacId") String beaconMacId) throws IOException {
        System.out.println("Find by '55ec9ad0ca450d5a90531a6' "+repository.findById("55ec9ad0ca450d5a90531a6"));
        System.out.println("Find by '0ef98d78-8aef-4c2c-a1df-77e237b6ec9d' "+repository.findById("0ef98d78-8aef-4c2c-a1df-77e237b6ec9d"));
        return getBeaconSubjectByBeaconMacId(beaconMacId);
    }

    @RequestMapping("/beacons/{beaconMacId}/calendar-events/free-busy")
    public @ResponseBody FreeBusyCalendar getCalendarEventsFreeBusyByBeaconMacIdAPI(@PathVariable("beaconMacId") String beaconMacId)
            throws IOException, GeneralSecurityException {
        BeaconSubject beaconSubject = getBeaconSubjectByBeaconMacId(beaconMacId);
        String beaconSubjectBusinessId = getBeaconSubjectBusinessId(beaconSubject).orElseThrow(() -> new BeaconBusinessIDNotFoundException(beaconMacId));                                                                                                                                      // exception
        return getCalendarEventsFreeBusyByCalendarId(beaconSubjectBusinessId);
    }

    private FreeBusyCalendar getCalendarEventsFreeBusyByCalendarId(String calendarId) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        // com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        debug.print("CalendarId={}", calendarId);
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        LocalDateTime localDt = LocalDateTime.ofInstant((new Date(now.getValue())).toInstant(), ZoneId.systemDefault());
        LocalDateTime localDtPlus1Week = localDt.plusWeeks(1);
        ZonedDateTime zonedDt = localDtPlus1Week.atZone(ZoneId.systemDefault());
        FreeBusyRequest fbreq = new FreeBusyRequest();
        fbreq.setTimeMin(now);
        fbreq.setTimeMax(new DateTime(Date.from(zonedDt.toInstant())));
        List<FreeBusyRequestItem> fbreqItems = new ArrayList<>();
        FreeBusyRequestItem fbreqItem = new FreeBusyRequestItem();
        fbreqItem.setId(calendarId);
        fbreqItems.add(fbreqItem);
        fbreq.setItems(fbreqItems);
        FreeBusyResponse fbres = service.freebusy().query(fbreq).execute();
        Map<String, FreeBusyCalendar> m = fbres.getCalendars();
        return m.get(calendarId);
    }

    private BeaconSubject getBeaconSubjectByBeaconMacId(String beaconMacId) {
        debug.print("beaconMacId={}", beaconMacId);
        Predicate<String> validateBeaconMacId = (String macId) -> {
            return macId.matches("[A-Za-z0-9]{2}(:[A-Za-z0-9]{2}){5}");
        };
        if (!validateBeaconMacId.test(beaconMacId)) {
            throw new BeaconMacIdNotValidException(beaconMacId);
        }
        return repositoryCustom.findBeaconSubjectByBeaconMac(beaconMacId).orElseThrow(() -> new BeaconNotFoundException(beaconMacId));
    }

    private Optional<String> getBeaconSubjectBusinessId(BeaconSubject beaconSubject) {
        debug.print("beaconSubject={}", beaconSubject);
        String businessId = null;
        if (beaconSubject != null) {
            businessId = beaconSubject.getBusinessId();
        }
        return Optional.ofNullable(businessId);
    }
}

@ControllerAdvice
class BeaconControllerAdvice {
    @ResponseBody
    @ExceptionHandler(BeaconNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors beaconNotFoundExceptionHandler(BeaconNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BeaconMacIdNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors beaconMacIdNotValidHandler(BeaconMacIdNotValidException ex) {
        VndErrors vndErrors = new VndErrors("error", ex.getMessage());
        return vndErrors;
    }
    
    @ResponseBody
    @ExceptionHandler(BeaconBusinessIDNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors beaconBusinessIDNotFoundExceptionHandler(BeaconBusinessIDNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }
    
}
