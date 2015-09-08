package com.weshaka.ole;

import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OleSvcApplication.class)
public class OleSvcApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMacIdRegex() {
        final Predicate<String> validateBeaconMacId = (String macId) -> {
            return macId.matches("[A-Za-z0-9]{2}(:[A-Za-z0-9]{2}){5}");
        };
        assertTrue(validateBeaconMacId.test("C1:5C:A0:2A:EC:F0"));
    }
}
