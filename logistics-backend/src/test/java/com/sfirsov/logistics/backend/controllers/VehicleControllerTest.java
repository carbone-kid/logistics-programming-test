package com.sfirsov.logistics.backend.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.LatLng;
import com.sfirsov.LogisticsBackendApplicationTests;
import com.sfirsov.logistics.backend.model.VehicleParams;
import com.sfirsov.logistics.backend.model.VehiclesData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class VehicleControllerTest extends LogisticsBackendApplicationTests {

    @Autowired
    private MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    final String testVehicleName = "test_vehicle_1";
    final String testVehicleName2 = "test_vehicle_2";
    final String testVehicleName3 = "test_vehicle_3";
    final double testVehicleLat = 52.0;
    final double testVehicleLng = -2.0;
    final double testDriveToLat = 52.5;
    final double testDriveToLng = -2.5;

    private VehicleParams makeVehicleParams(String vehicleName) {
        VehicleParams vehicleParams = new VehicleParams();
        vehicleParams.setName(vehicleName);
        vehicleParams.setLat(testVehicleLat);
        vehicleParams.setLng(testVehicleLng);
        return vehicleParams;
    }

    private VehicleParams makeDriveToParams(String vehicleName, double lat, double lng) {
        VehicleParams vehicleParams = new VehicleParams();
        vehicleParams.setName(vehicleName);
        vehicleParams.setLat(lat);
        vehicleParams.setLng(lng);
        return vehicleParams;
    }

    private void addVehicle(String vehicleName) throws Exception {
        VehicleParams vehicleParams1 = makeVehicleParams(vehicleName);

        this.mvc.perform(put("/api/vehicles/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(vehicleParams1)))
                .andExpect(status().isOk());
    }

    @Before
    public void setUp() throws Exception {
        this.mvc.perform(delete("/api/vehicles/delete-all"));
    }

    @Test
    public void getVehiclesDataTest() throws Exception {

        List<VehicleParams> vehicles = new ArrayList<>();
        VehiclesData vehiclesData = new VehiclesData(vehicles);
        vehicles.add(makeVehicleParams(testVehicleName));

        addVehicle(testVehicleName);

        this.mvc.perform(get("/api/vehicles/").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(vehiclesData)));
    }

    @Test
    public void addVehicleTest() throws Exception {
        addVehicle(testVehicleName);
    }

    @Test
    public void deleteAllVehiclesTest() throws Exception {
        addVehicle(testVehicleName);
        addVehicle(testVehicleName2);
        addVehicle(testVehicleName3);

        this.mvc.perform(delete("/api/vehicles/delete-all"))
                .andExpect(status().isOk());

        List<VehicleParams> vehicles = new ArrayList<>();
        VehiclesData vehiclesData = new VehiclesData(vehicles);

        this.mvc.perform(get("/api/vehicles/").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(vehiclesData)));
    }

    @Test
    public void deleteVehicleTest() throws Exception {
        addVehicle(testVehicleName);
        addVehicle(testVehicleName2);

        this.mvc.perform(delete("/api/vehicles/" + testVehicleName + "/delete"))
                .andExpect(status().isOk());

        List<VehicleParams> vehicleParams = new ArrayList<>();
        vehicleParams.add(makeVehicleParams(testVehicleName2));
        VehiclesData vehiclesData = new VehiclesData(vehicleParams);

        this.mvc.perform(get("/api/vehicles/").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(vehiclesData)));
    }

    @Test
    public void driveToTest() throws Exception {
        addVehicle(testVehicleName);

        VehicleParams params = makeDriveToParams(testVehicleName, testDriveToLat, testDriveToLng);

        this.mvc.perform(post("/api/vehicles/drive").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());

        TimeUnit.SECONDS.sleep(5);

        MvcResult result = this.mvc.perform(get("/api/vehicles/" + testVehicleName + "/path").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<LatLngTest> path = mapper.readValue(content, new TypeReference<ArrayList<LatLngTest>>(){});

        Assert.assertNotNull(path);
        Assert.assertNotEquals(path.size(), 0);
        Assert.assertNotNull(path.get(0));
        Assert.assertEquals(testVehicleLat, path.get(0).lat, 0.01);
        Assert.assertEquals(testVehicleLng, path.get(0).lng, 0.01);
        Assert.assertEquals(testDriveToLat, path.get(path.size()-1).lat, 0.01);
        Assert.assertEquals(testDriveToLng, path.get(path.size()-1).lng, 0.01);
    }
}

// Class adds default constructor to google.maps.LatLng to simplify deserialization of LatLng using Jackson
class LatLngTest extends LatLng {
    public LatLngTest(double lat, double lng) {
        super(lat, lng);
    }

    public LatLngTest() {
        super(0, 0);
    }
}
