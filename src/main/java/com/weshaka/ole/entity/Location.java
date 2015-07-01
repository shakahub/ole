package com.weshaka.ole.entity;

public class Location {
	private double longitude;
	private double latitude;
	private String city;
	private String state;
	private String country;

	@Override
	public String toString() {
		return String
				.format("Location[longitude=%s,latitude=%s,city=%s,state=%s,country=%s]",
						longitude, latitude, city, state, country);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
