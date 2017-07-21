package com.sfirsov.logistics.backend.service;

import com.google.maps.model.LatLng;
import com.sfirsov.LogisticsBackendApplicationTests;
import com.sfirsov.logistics.backend.model.VehicleParams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VehicleServiceTest extends LogisticsBackendApplicationTests {

    @Autowired
    VehicleService vehicleService;

    final String testVehicleName = "test_vehicle";
    final double testVehicleLat = 52.0;
    final double testVehicleLng = -2.0;

    private void addTestVehicle(String vehicleName) {
        VehicleParams vehicleParams = new VehicleParams();
        vehicleParams.setName(testVehicleName);
        vehicleParams.setLat(testVehicleLat);
        vehicleParams.setLng(testVehicleLng);
        vehicleService.addVehicle(vehicleParams);
    }

    @Before
    public void setUp() {
        vehicleService.deleteAllVehicles();
    }

    @Test
    public void generalTest() {
        Assert.assertNotNull(vehicleService);
        Assert.assertNotNull(vehicleService.getVehiclesParams());
    }

    @Test
    public void addVehicleTest() {
        int initialVehiclesCount = vehicleService.getVehiclesParams().size();
        addTestVehicle(testVehicleName);
        Assert.assertEquals("Wrong number of vehicles", initialVehiclesCount, vehicleService.getVehiclesParams().size() - 1);
    }

    @Test
    public void deleteVehicleTest() {
        int initialVehiclesCount = vehicleService.getVehiclesParams().size();
        addTestVehicle(testVehicleName);
        vehicleService.deleteVehicle(testVehicleName);
        Assert.assertEquals("Wrong number of vehicles", initialVehiclesCount, vehicleService.getVehiclesParams().size());
    }

    @Test
    public void deleteAllVehiclesTest() {
        addTestVehicle(testVehicleName);
        addTestVehicle("test_vehicle2");
        addTestVehicle("test_vehicle3");
        vehicleService.deleteAllVehicles();
        Assert.assertEquals("Vehicles count should be 0 after deleting all vehicles",
                0, vehicleService.getVehiclesParams().size());
    }

    @Test
    public void driveToTest() throws InterruptedException {
        addTestVehicle(testVehicleName);
        VehicleParams vehicleDestinationParams = new VehicleParams();
        vehicleDestinationParams.setName(testVehicleName);
        vehicleDestinationParams.setLat(52.4);
        vehicleDestinationParams.setLng(-1.9);
        vehicleService.driveTo(vehicleDestinationParams);

        TimeUnit.SECONDS.sleep(5);
        List<LatLng> path = vehicleService.getVehiclePath(testVehicleName);

        Assert.assertNotNull(path);
        Assert.assertNotEquals(path.size(), 0);
        Assert.assertNotNull(path.get(0));
        Assert.assertEquals(testVehicleLat, path.get(0).lat, 0.01);
        Assert.assertEquals(testVehicleLng, path.get(0).lng, 0.01);
        Assert.assertEquals(vehicleDestinationParams.getLat(), path.get(path.size()-1).lat, 0.01);
        Assert.assertEquals(vehicleDestinationParams.getLng(), path.get(path.size()-1).lng, 0.01);
    }
}
