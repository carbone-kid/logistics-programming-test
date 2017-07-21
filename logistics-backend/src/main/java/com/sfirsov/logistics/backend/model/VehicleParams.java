package com.sfirsov.logistics.backend.model;

import com.sfirsov.logistics.backend.service.Vehicle;

public class VehicleParams {
    private String name;
    private double lat;
    private double lng;

    public VehicleParams() {}

    public VehicleParams(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public VehicleParams(Vehicle vehicle) {
        this.lat = vehicle.getCurrentPosition().lat;
        this.lng = vehicle.getCurrentPosition().lng;
        this.name = vehicle.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
