package com.weshaka.ole.entity;

public class Beacon {

    private String beaconId;
    private String brand;
    private BeaconType beaconType;
    private String upc;
    private double powerlevel;
    private String mac;
    private String model;
    private String color;

    public String getBeaconId() {
        return beaconId;
    }

    public BeaconType getBeaconType() {
        return beaconType;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public String getMac() {
        return mac;
    }

    public String getModel() {
        return model;
    }

    public double getPowerlevel() {
        return powerlevel;
    }

    public String getUpc() {
        return upc;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public void setBeaconType(BeaconType beaconType) {
        this.beaconType = beaconType;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPowerlevel(double powerlevel) {
        this.powerlevel = powerlevel;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    @Override
    public String toString() {
        return String.format("Beacon[beaconId=%s,brand=%s,model=%s,type=%s,upc=%s,powerlevel=%s,mac=%s,color=%s]", beaconId, brand, model, beaconType, upc, powerlevel, mac, color);
    }
}
