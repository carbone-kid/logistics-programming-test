package com.sfirsov.logistics.backend.model;

import java.util.List;

public class VehiclesData {
    private List<VehicleParams> vehicles;

    public VehiclesData(List<VehicleParams> vehicles) {
        this.vehicles = vehicles;
    }

    public List<VehicleParams> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleParams> vehicles) {
        this.vehicles = vehicles;
    }
}
