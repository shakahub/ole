package com.weshaka.ole.entity;

import java.util.Optional;

import org.springframework.data.annotation.Id;

public class BeaconSubject {
	@Id
	private String id;
	private String name;
	private BeaconSubjectType beaconSubjectType;
	private Location location;
	private Beacon beacon;

	public BeaconSubject() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Beacon getBeacon() {
		return beacon;
	}

	public void setBeacon(Beacon beacon) {
		this.beacon = beacon;
	}

	public BeaconSubjectType getBeaconSubjectType() {
		return beaconSubjectType;
	}

	public void setBeaconSubjectType(BeaconSubjectType beaconSubjectType) {
		this.beaconSubjectType = beaconSubjectType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
