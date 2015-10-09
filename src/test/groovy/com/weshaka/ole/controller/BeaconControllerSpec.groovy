package com.weshaka.ole.controller;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule
import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule
import static com.lordofthejars.nosqlunit.redis.RedisRule.RedisRuleBuilder.newRedisRule

import org.junit.ClassRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Stepwise

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.services.calendar.model.FreeBusyCalendar
import com.lordofthejars.nosqlunit.annotation.Selective
import com.lordofthejars.nosqlunit.annotation.UsingDataSet
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule
import com.lordofthejars.nosqlunit.mongodb.SpringMongoDbRule
import com.weshaka.google.calendar.ole.pojo.CalendarEvent
import com.weshaka.ole.OleSvcApplication
import com.weshaka.ole.entity.BeaconSubject

@ActiveProfiles(["test"])
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = OleSvcApplication.class)
@WebIntegrationTest("server.port:8090")
@Stepwise
class BeaconControllerSpec extends Specification {

    @ClassRule
    public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule().build();

    @Rule
    public MongoDbRule mongoDbRule = new SpringMongoDbRule(mongoDb().databaseName("ole-svc-test").connectionIdentifier("mongodb").build());

    @Autowired
    private ApplicationContext appliactionContext;

    void "Testing spock works!"(){
        expect:
        true
    }
    void "Should return 200 from /calendar-envents!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/calendar-events", String.class)

        then:
        entity.statusCode == HttpStatus.OK
    }
    @UsingDataSet(withSelectiveLocations =
    [
        @Selective(identifier = "mongodb", locations = [
            "/BeaconSubjectWithDemoBeacon.json"
        ])
    ], loadStrategy = LoadStrategyEnum.INSERT)
    void "Should return 200 from /beacons/C1:5C:A0:2A:EC:F0!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/beacons/C1:5C:A0:2A:EC:F0", BeaconSubject.class)

        then:
        BeaconSubject beaconSubject = entity.getBody()
        entity.statusCode == HttpStatus.OK
        beaconSubject.getBeacon().getMac()=="C1:5C:A0:2A:EC:F0"
    }

    void "Should return 200 from /calendar-events/weshaka.com_38383737363330392d363936@resource.calendar.google.com/free-busy!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/calendar-events/weshaka.com_38383737363330392d363936@resource.calendar.google.com/free-busy", FreeBusyCalendar.class)

        then:
        FreeBusyCalendar freeBusyCalendar = entity.getBody()
        entity.statusCode == HttpStatus.OK
    }

    void "Should return 200 from /calendar-events/weshaka.com_38383737363330392d363936@resource.calendar.google.com!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/calendar-events/weshaka.com_38383737363330392d363936@resource.calendar.google.com", List.class)

        then:
        List<CalendarEvent> list = entity.getBody()
        entity.statusCode == HttpStatus.OK
    }
    @Ignore //TODO: Fix and enable
    void "Should return 200 from /calendar-events 'POST' for creating new calendar event!"() {
        when:
        //        CreateCalendarEventRequest body = new CreateCalendarEventRequest()
        //        CalendarEvent calendarEvent = new CalendarEvent()
        //        CalendarEventCreator calendarEventCreator = new CalendarEventCreator()
        //        calendarEventCreator.setEmail("admin@weshaka.com")
        //        calendarEventCreator.setId("admin")
        //        calendarEvent.setCreator(calendarEventCreator)
        //        calendarEvent.setDescription("This is description")
        //        calendarEvent.setLocation("Rainier Location")
        //        calendarEvent.setSummary("This is summary")
        //        String startTime = "2015-10-12T10:00:00";
        //        String endTime = "2015-10-12T11:00:00";
        //        LocalDateTime startTimeLocal = LocalDateTime.parse(startTime)
        //        LocalDateTime endTimeLocal =LocalDateTime.parse(endTime)
        //        calendarEvent.setStartDateTime(startTimeLocal)
        //        calendarEvent.setEndDateTime(endTimeLocal)
        //        List<EventAttendee> eventAttendees = new ArrayList<>()
        //        EventAttendee eventAttendee = new EventAttendee()
        //        eventAttendee.setId("admin")
        //        eventAttendee.setComment("This is comment")
        //        eventAttendees.add(eventAttendee)
        //        calendarEvent.setEventAttendees(eventAttendees)
        //        body.setCalendarEvent(calendarEvent)
        ObjectMapper objectMapper = new ObjectMapper()
        String jsonString = objectMapper.readTree("""
{
  "calendarEvent": {
  "summary": "Google I/O 2015",
  "location": "Rainier",
  "description": "A chance to hear more about Google developer products.",
  "startDateTime": [
2015,10,10,8,0,0,0
],
  "endDateTime": [
2015,10,10,10,0,0,0
],
  "eventAttendees": [
    {"email": "admin@weshaka.com"},
    {"email": "yixing@gmail.com"}
],
"creator":{
"email":"admin@weshaka.com",
"id":"admin@weshaka.com"
},
"recurrence": [
"RRULE:FREQ=DAILY;COUNT=2"
]
}
}""")
        System.out.println("jsonString="+jsonString)
        JsonNode requestJson = objectMapper.readTree(jsonString)
        RequestEntity request = RequestEntity.post(new URI("http://localhost:8090/calendar-events")).accept(MediaType.APPLICATION_JSON).body(requestJson)
        RestTemplate restTemplate = new RestTemplate()
        //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<CalendarEvent> response = restTemplate.exchange(request, CalendarEvent.class)

        then:
        CalendarEvent calendarEventResponse = response.getBody()
        response.statusCode == HttpStatus.OK
    }

    @UsingDataSet(withSelectiveLocations =
    [
        @Selective(identifier = "mongodb", locations = [
            "/BeaconSubjectWithDemoBeacon.json"
        ])
    ], loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    void "Should return 404 from /beacons/C1:5C:A0:2A:EC:FF!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/beacons/C1:5C:A0:2A:EC:FF", BeaconSubject.class)

        then:
        final HttpClientErrorException exception = thrown()
        exception.statusCode==HttpStatus.NOT_FOUND
    }

    @UsingDataSet(withSelectiveLocations =
    [
        @Selective(identifier = "mongodb", locations = [
            "/BeaconSubjectWithDemoBeacon.json"
        ])
    ], loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    void "Should return 400 from /beacons/C1:5C:A0:2A:EC!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/beacons/C1:5C:A0:2A:EC", BeaconSubject.class)

        then:
        final HttpClientErrorException exception = thrown()
        exception.statusCode==HttpStatus.BAD_REQUEST
    }
    @UsingDataSet(withSelectiveLocations =
    [
        @Selective(identifier = "mongodb", locations = [
            "/BeaconSubjectWithDemoBeaconWithoutBusinessId.json"
        ])
    ], loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    void "Should return 404 from /beacons/C1:5C:A0:2A:EC:AA/calendar-events/free-busy!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/beacons/C1:5C:A0:2A:EC:AA/calendar-events/free-busy", FreeBusyCalendar.class)

        then:
        final HttpClientErrorException exception = thrown()
        exception.statusCode==HttpStatus.NOT_FOUND
    }
    @UsingDataSet(withSelectiveLocations =
    [
        @Selective(identifier = "mongodb", locations = [
            "/BeaconSubjectWithDemoBeaconWithEmptyBusinessId.json"
        ])
    ], loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    void "Again should return 404 from /beacons/C1:5C:A0:2A:EC:AB/calendar-events/free-busy!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8090/beacons/C1:5C:A0:2A:EC:AB/calendar-events/free-busy", FreeBusyCalendar.class)

        then:
        final HttpClientErrorException exception = thrown()
        exception.statusCode==HttpStatus.NOT_FOUND
    }
}