package com.weshaka.ole.entity;

import org.springframework.data.annotation.Id;

public class Beacon {
	@Id
	private String id;
	private String brand;
	private BeaconType beaconType;
	private String upc;
	private double powerlevel;
	private String mac;
	private String model;
	private String color;

	public String getId() {
		return id;
	}

	public BeaconType getBeaconType() {
		return beaconType;
	}

	public void setBeaconType(BeaconType beaconType) {
		this.beaconType = beaconType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public double getPowerlevel() {
		return powerlevel;
	}

	public void setPowerlevel(double powerlevel) {
		this.powerlevel = powerlevel;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return String
				.format("Beacon[id=%s,brand=%s,model=%s,type=%s,upc=%s,powerlevel=%d,mac=%s,color=%s]",
						id, brand, model, beaconType, upc, powerlevel, mac,
						color);
	}
}
