package com.sfirsov.logistics.backend.controllers;

import com.google.maps.model.LatLng;
import com.sfirsov.logistics.backend.model.VehicleParams;
import com.sfirsov.logistics.backend.model.VehiclesData;
import com.sfirsov.logistics.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    VehicleService vehicleService;

    @RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiclesData getVehicles(@RequestParam(defaultValue="0") String vehicleName) {
        return new VehiclesData(vehicleService.getVehiclesParams());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addVehicle(@RequestBody VehicleParams vehicleParams) {
        vehicleService.addVehicle(vehicleParams);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/drive", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void driveTo(@RequestBody VehicleParams vehicleParams) {
        vehicleService.driveTo(vehicleParams);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{vehicleName}/path", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LatLng> getVehiclePath(@PathVariable String vehicleName) {
        return vehicleService.getVehiclePath(vehicleName);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete-all")
    public void deleteAllVehicles() {
        vehicleService.deleteAllVehicles();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{vehicleName}/delete")
    public void deleteVehicle(@PathVariable String vehicleName) {
        vehicleService.deleteVehicle(vehicleName);
    }
}
