package com.sfirsov.logistics.backend.service;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.sfirsov.logistics.backend.model.VehicleParams;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class VehicleService {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, Future<LatLng>> futures = new HashMap();
    private Map<String, Vehicle> vehicles = new HashMap();
    private GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBnryctJf9zu9h02StvthhTm9vUzKKfMt4");
    private int nameGeneratorCounter = 0;

    private String generateName() {
        nameGeneratorCounter++;
        return String.format("Vehicle_%d", nameGeneratorCounter);
    }

    public List<VehicleParams> getVehiclesParams() {
        return vehicles.entrySet().stream().map(v -> new VehicleParams(v.getValue())).collect(Collectors.toList());
    }

    public List<LatLng> getVehiclePath(String vehicleName) {
        return vehicles.containsKey(vehicleName) ? vehicles.get(vehicleName).getPath() : null;
    }

    public void addVehicle(VehicleParams vehicleParams) {
        if(vehicleParams.getName() == null || vehicleParams.getName().isEmpty()) {
            vehicleParams.setName(generateName());
        }

        if(vehicles.containsKey(vehicleParams.getName())) {
            deleteVehicle(vehicleParams.getName());
        }

        vehicles.put(vehicleParams.getName(), new Vehicle(context, vehicleParams));
    }

    public void deleteVehicle(String vehicleName) {
        if(futures.containsKey(vehicleName)) {
            futures.get(vehicleName).cancel(true);
            futures.remove(vehicleName);
        }
        if(vehicles.containsKey(vehicleName)) {
            vehicles.remove(vehicleName);
        }
    }

    public void deleteAllVehicles() {
        futures.forEach((k, v) -> v.cancel(true));
        futures.clear();
        vehicles.clear();
    }

    public void driveTo(VehicleParams vehicleParams) {
        if(futures.containsKey(vehicleParams.getName())) {
            futures.get(vehicleParams.getName()).cancel(true);
        }
        futures.put(vehicleParams.getName(), vehicles.get(vehicleParams.getName()).driveTo(new LatLng(vehicleParams.getLat(), vehicleParams.getLng()), executorService));
    }
}
