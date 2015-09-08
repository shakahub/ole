package com.weshaka.ole.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class BeaconSubject {
    @Id
    private ObjectId id;
    private String beaconSubjectId;
    private String name;
    private BeaconSubjectType beaconSubjectType;
    private Location location;
    private Beacon beacon;
    private String businessId;

    public BeaconSubject() {

    }

    public Beacon getBeacon() {
        return beacon;
    }

    public String getBeaconSubjectId() {
        return beaconSubjectId;
    }

    public BeaconSubjectType getBeaconSubjectType() {
        return beaconSubjectType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public ObjectId getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public void setBeaconSubjectId(String beaconSubjectId) {
        this.beaconSubjectId = beaconSubjectId;
    }

    public void setBeaconSubjectType(BeaconSubjectType beaconSubjectType) {
        this.beaconSubjectType = beaconSubjectType;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }
}
