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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.weshaka.google.calendar.ole.CalendarServiceFactory;
import com.weshaka.google.calendar.ole.pojo.CalendarEvent;
import com.weshaka.google.calendar.ole.pojo.CalendarEventCreator;
import com.weshaka.google.calendar.ole.pojo.CreateCalendarEventRequest;
import com.weshaka.ole.entity.BeaconSubject;
import com.weshaka.ole.exceptions.BeaconBusinessIDNotFoundException;
import com.weshaka.ole.exceptions.BeaconMacIdNotValidException;
import com.weshaka.ole.exceptions.BeaconNotFoundException;
import com.weshaka.ole.exceptions.CalendarServiceNotResponseException;
import com.weshaka.ole.repository.BeaconSubjectRepository;
import com.weshaka.ole.repository.BeaconSubjectRepositoryCustom;
import com.weshaka.service.CalendarService;

/**
 * @author ema
 */
@RestController
public class BeaconController extends CommonController {

    @Autowired
    private BeaconSubjectRepository repository;

    @Autowired
    private BeaconSubjectRepositoryCustom repositoryCustom;

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "/calendar-events", method = RequestMethod.POST)
    public @ResponseBody CalendarEvent createCalendarEventAPI(@RequestBody CreateCalendarEventRequest request) throws IOException, GeneralSecurityException {

        final CalendarEvent calendarEvent = request.getCalendarEvent();
        Event event = new Event().setSummary(calendarEvent.getSummary()).setLocation(calendarEvent.getLocation()).setDescription(calendarEvent.getDescription());
        final Function<LocalDateTime, EventDateTime> convertDateTimeFunc = localDateTime -> {
            final DateTime gLocalDateTime = new DateTime(Date.from(localDateTime.atZone(ZoneId.of("America/Los_Angeles")).toInstant()));// TODO: Change zoneId to param
            final EventDateTime eventDateTime = new EventDateTime().setDateTime(gLocalDateTime).setTimeZone("America/Los_Angeles");// TODO: Change Timezone to param
            return eventDateTime;
        };
        final EventDateTime start = convertDateTimeFunc.apply(calendarEvent.getStartDateTime());
        event.setStart(start);
        final EventDateTime end = convertDateTimeFunc.apply(calendarEvent.getEndDateTime());
        event.setEnd(end);
        if (calendarEvent.getRecurrence() != null)
            event.setRecurrence(Arrays.asList(calendarEvent.getRecurrence()));
        event.setAttendees(calendarEvent.getEventAttendees());
        final EventReminder[] reminderOverrides = new EventReminder[] { new EventReminder().setMethod("email").setMinutes(24 * 60), new EventReminder().setMethod("popup").setMinutes(10), };
        final Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        final String calendarId = calendarEvent.getCreator().getId();
        final com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());

        return calendarEvent;
    }

    @RequestMapping(value = "/calendar-events/{calendarId}/{eventId}", method = RequestMethod.DELETE)
    public @ResponseBody CalendarEvent deleteCalendarEventAPI(@PathVariable("calendarId") String calendarId, @PathVariable("eventId") String eventId) throws IOException, GeneralSecurityException {
        final com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        final Event event = service.events().get(calendarId, eventId).execute();
        service.events().delete(calendarId, eventId).execute();
        // TODO: Selma
        final CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.getEventAttendees().addAll(event.getAttendees());
        calendarEvent.setDescription(event.getDescription());
        DateTime start = event.getStart().getDateTime();
        if (start == null) {
            start = event.getStart().getDate();
        }
        calendarEvent.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(), ZoneId.systemDefault()));
        DateTime end = event.getEnd().getDateTime();
        if (end == null) {
            end = event.getEnd().getDate();
        }
        calendarEvent.setEndDateTime(LocalDateTime.ofInstant((new Date(end.getValue())).toInstant(), ZoneId.systemDefault()));
        calendarEvent.setEventId(eventId);
        calendarEvent.setLocation(event.getLocation());
        calendarEvent.setSummary(event.getSummary());
        final CalendarEventCreator creator = new CalendarEventCreator();
        if (event.getCreator() != null) {
            creator.setEmail(event.getCreator().getEmail());
            creator.setId(event.getCreator().getId());
        }
        calendarEvent.setCreator(creator);
        if (event.getRecurrence() != null)
            calendarEvent.setRecurrence(event.getRecurrence().stream().toArray(String[]::new));
        calendarEvent.setEventId(event.getId());
        return calendarEvent;
    }

    private Optional<String> getBeaconSubjectBusinessId(BeaconSubject beaconSubject) {
        debug.print("beaconSubject={}", beaconSubject);
        String businessId = null;
        if (beaconSubject != null) {
            businessId = !StringUtils.isEmptyOrNull(beaconSubject.getBusinessId()) ? beaconSubject.getBusinessId() : businessId;
        }
        return Optional.ofNullable(businessId);
    }

    private BeaconSubject getBeaconSubjectByBeaconMacId(String beaconMacId) {
        debug.print("beaconMacId={}", beaconMacId);
        final Predicate<String> validateBeaconMacId = (String macId) -> {
            return macId.matches("[A-Za-z0-9]{2}(:[A-Za-z0-9]{2}){5}");
        };
        if (!validateBeaconMacId.test(beaconMacId)) {
            throw new BeaconMacIdNotValidException(beaconMacId);
        }
        return repositoryCustom.findBeaconSubjectByBeaconMac(beaconMacId).orElseThrow(() -> new BeaconNotFoundException(beaconMacId));
    }

    @RequestMapping("/beacons/{beaconMacId}")
    @Cacheable("beacon")
    public @ResponseBody BeaconSubject getBeaconSubjectByBeaconMacIdAPI(@PathVariable("beaconMacId") String beaconMacId) throws IOException {
        return getBeaconSubjectByBeaconMacId(beaconMacId);
    }

    @RequestMapping("/calendar-events")
    public @ResponseBody List<CalendarEvent> getCalendarEventsAPI() throws IOException, GeneralSecurityException {
        final Future<Events> futureEvents = calendarService.getPrimaryEvents("startTime", 10);
        Events events = null;
        try {
            events = futureEvents.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e1) {
            throw new CalendarServiceNotResponseException(e1.getMessage());
        }

        final List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (events != null) {
            final List<Event> items = events.getItems();
            if (items.size() == 0) {
                debug.print("No upcoming events found for {}", "default primary");
            } else {
                debug.print("Upcoming events {}", "default primary");
                items.forEach(event -> {
                    final CalendarEvent e = new CalendarEvent();
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    debug.print("eventStart={}" + start);
                    e.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(), ZoneId.systemDefault()));
                    final List<EventAttendee> attendees = event.getAttendees();
                    if (attendees != null)
                        e.getEventAttendees().addAll(attendees);
                    e.setSummary(event.getSummary());
                    e.setEventId(event.getId());
                    calendarEvents.add(e);
                });
            }
        }
        return calendarEvents;
    }

    @RequestMapping("/beacons/{beaconMacId}/calendar-events")
    public @ResponseBody List<CalendarEvent> getCalendarEventsByBeaconMacIdAPI(@PathVariable("beaconMacId") String beaconMacId) throws IOException, GeneralSecurityException {
        final BeaconSubject beaconSubject = getBeaconSubjectByBeaconMacId(beaconMacId);
        final String beaconSubjectBusinessId = getBeaconSubjectBusinessId(beaconSubject).orElseThrow(() -> new BeaconBusinessIDNotFoundException(beaconMacId)); // exception
        return getCalendarEventsByCalendarId(beaconSubjectBusinessId);
    }

    private List<CalendarEvent> getCalendarEventsByCalendarId(String calendarId) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        // com.google.api.services.calendar.model.Calendar class.
        final com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        debug.print("CalendarId={}", calendarId);
        // List the next 10 events from the primary calendar.
        final DateTime now = new DateTime(System.currentTimeMillis());
        final Events events = service.events().list(calendarId).setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute();
        final List<Event> items = events.getItems();
        final List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (items.size() == 0) {
            debug.print("No upcoming events found for {}", calendarId);
        } else {
            debug.print("Upcoming events for {}", calendarId);
            items.forEach(event -> {
                final CalendarEvent e = new CalendarEvent();
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                e.setStartDateTime(LocalDateTime.ofInstant((new Date(start.getValue())).toInstant(), ZoneId.systemDefault()));
                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate();
                }
                e.setEndDateTime(LocalDateTime.ofInstant((new Date(end.getValue())).toInstant(), ZoneId.systemDefault()));
                final List<EventAttendee> attendees = event.getAttendees();
                if (attendees != null)
                    e.getEventAttendees().addAll(attendees);
                e.setSummary(event.getSummary());
                final CalendarEventCreator creator = new CalendarEventCreator();
                if (event.getCreator() != null) {
                    creator.setEmail(event.getCreator().getEmail());
                    creator.setId(event.getCreator().getId());
                }
                e.setCreator(creator);
                if (event.getRecurrence() != null)
                    e.setRecurrence(event.getRecurrence().stream().toArray(String[]::new));
                e.setEventId(event.getId());
                calendarEvents.add(e);
            });
        }
        return calendarEvents;
    }

    @RequestMapping("/calendar-events/{calendarId:.+}")
    public @ResponseBody List<CalendarEvent> getCalendarEventsByCalendarIdAPI(@PathVariable("calendarId") String calendarId) throws IOException, GeneralSecurityException {
        return getCalendarEventsByCalendarId(calendarId);
    }

    @RequestMapping("/beacons/{beaconMacId}/calendar-events/free-busy")
    public @ResponseBody FreeBusyCalendar getCalendarEventsFreeBusyByBeaconMacIdAPI(@PathVariable("beaconMacId") String beaconMacId) throws IOException, GeneralSecurityException {
        final BeaconSubject beaconSubject = getBeaconSubjectByBeaconMacId(beaconMacId);
        final String beaconSubjectBusinessId = getBeaconSubjectBusinessId(beaconSubject).orElseThrow(() -> new BeaconBusinessIDNotFoundException(beaconMacId)); // exception
        return getCalendarEventsFreeBusyByCalendarId(beaconSubjectBusinessId);
    }

    private FreeBusyCalendar getCalendarEventsFreeBusyByCalendarId(String calendarId) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        // com.google.api.services.calendar.model.Calendar class.
        final com.google.api.services.calendar.Calendar service = CalendarServiceFactory.getCalendarService();
        debug.print("CalendarId={}", calendarId);
        // List the next 10 events from the primary calendar.
        final DateTime now = new DateTime(System.currentTimeMillis());
        final LocalDateTime localDt = LocalDateTime.ofInstant((new Date(now.getValue())).toInstant(), ZoneId.systemDefault());
        final LocalDateTime localDtPlus1Week = localDt.plusWeeks(1);
        final ZonedDateTime zonedDt = localDtPlus1Week.atZone(ZoneId.systemDefault());
        final FreeBusyRequest fbreq = new FreeBusyRequest();
        fbreq.setTimeMin(now);
        fbreq.setTimeMax(new DateTime(Date.from(zonedDt.toInstant())));
        final List<FreeBusyRequestItem> fbreqItems = new ArrayList<>();
        final FreeBusyRequestItem fbreqItem = new FreeBusyRequestItem();
        fbreqItem.setId(calendarId);
        fbreqItems.add(fbreqItem);
        fbreq.setItems(fbreqItems);
        final FreeBusyResponse fbres = service.freebusy().query(fbreq).execute();
        final Map<String, FreeBusyCalendar> m = fbres.getCalendars();
        final FreeBusyCalendar freeBusyCalendar = m.get(calendarId);
        return freeBusyCalendar;
    }

    @RequestMapping("/calendar-events/{calendarId:.+}/free-busy")
    public @ResponseBody FreeBusyCalendar getCalendarEventsFreeBusyByCalendarIdAPI(@PathVariable("calendarId") String calendarId) throws IOException, GeneralSecurityException {
        return getCalendarEventsFreeBusyByCalendarId(calendarId);
    }
}

@ControllerAdvice
class BeaconControllerAdvice {
    @ResponseBody
    @ExceptionHandler(BeaconBusinessIDNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors beaconBusinessIDNotFoundExceptionHandler(BeaconBusinessIDNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BeaconMacIdNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors beaconMacIdNotValidHandler(BeaconMacIdNotValidException ex) {
        final VndErrors vndErrors = new VndErrors("error", ex.getMessage());
        return vndErrors;
    }

    @ResponseBody
    @ExceptionHandler(BeaconNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors beaconNotFoundExceptionHandler(BeaconNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(CalendarServiceNotResponseException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    VndErrors calendarServiceNotResponseHandler(CalendarServiceNotResponseException ex) {
        final VndErrors vndErrors = new VndErrors("error", ex.getMessage());
        return vndErrors;
    }

}
