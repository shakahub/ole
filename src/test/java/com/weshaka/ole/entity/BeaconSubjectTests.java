/**
 *
 */
package com.weshaka.ole.entity;

import static org.junit.Assert.assertEquals;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author ema
 */
@RunWith(MockitoJUnitRunner.class)
public class BeaconSubjectTests {
    @Mock
    private Beacon beacon;
    @Mock
    private Location location;
    @Mock
    private ObjectId objectId;
    private BeaconSubject beaconSubject;

    @Before
    public void setUp() {
        beaconSubject = new BeaconSubject();
    }

    @Test
    public void shouldSetValues() throws Exception {
        beaconSubject.setBeacon(beacon);
        assertEquals(beacon, beaconSubject.getBeacon());
        beaconSubject.setBeaconSubjectType(BeaconSubjectType.ConferenceRoom);
        assertEquals(BeaconSubjectType.ConferenceRoom, beaconSubject.getBeaconSubjectType());
        beaconSubject.setLocation(location);
        assertEquals(location, beaconSubject.getLocation());
        beaconSubject.setId(objectId);
        assertEquals(objectId, beaconSubject.getId());
        beaconSubject.setBeaconSubjectId("TestSubjectId");
        beaconSubject.setName("Test");
        beaconSubject.setBusinessId("BusId");
        assertEquals("TestSubjectId", beaconSubject.getBeaconSubjectId());
        assertEquals("Test", beaconSubject.getName());
        assertEquals("BusId", beaconSubject.getBusinessId());
    }
}
