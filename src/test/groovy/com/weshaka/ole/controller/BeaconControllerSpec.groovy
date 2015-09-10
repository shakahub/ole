package com.weshaka.ole.controller;

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate

import spock.lang.Specification
import spock.lang.Stepwise

import com.weshaka.ole.OleSvcApplication

@ActiveProfiles("prod")
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = OleSvcApplication.class)
@WebIntegrationTest("server.port:8090")
@Stepwise
class BeaconControllerSpec extends Specification {

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
}